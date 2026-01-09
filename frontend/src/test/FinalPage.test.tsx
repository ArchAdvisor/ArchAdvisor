import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import { beforeEach, afterEach, expect, test, vi } from "vitest";
import FinalStackPage from "../FinalPage";

function LocationSpy() {
    const loc = useLocation();
    return (
        <pre data-testid="location">
            {JSON.stringify({ pathname: loc.pathname, state: loc.state ?? null }, null, 2)}
        </pre>
    );
}

type Tech = { id: number | string; name: string };
type Recommendation = { technology: Tech; score: number; warnings?: string[] };

const rec = (id: number, name: string): Recommendation => ({
    technology: { id, name },
    score: 0.9,
    warnings: [],
});

function renderFinalPage(
    initialEntry: { pathname: string; state?: any } | string = { pathname: "/final" }
) {
    const entry =
        typeof initialEntry === "string" ? { pathname: initialEntry } : initialEntry;

    return render(
        <MemoryRouter initialEntries={[entry as any]}>
            <Routes>
                <Route path="/" element={<LocationSpy />} />
                <Route path="/results" element={<LocationSpy />} />
                <Route path="/final" element={<FinalStackPage />} />
            </Routes>
        </MemoryRouter>
    );
}

let anchorClickMock: ReturnType<typeof vi.fn>;

beforeEach(() => {
    vi.restoreAllMocks();

    vi.stubGlobal("fetch", vi.fn());

    vi.stubGlobal("URL", {
        ...URL,
        createObjectURL: vi.fn(() => "blob:mock"),
        revokeObjectURL: vi.fn(),
    });

    const originalCreateElement = document.createElement.bind(document);

    anchorClickMock = vi.fn();

    vi.spyOn(document, "createElement").mockImplementation((tagName: string) => {
        const el = originalCreateElement(tagName);

        if (tagName.toLowerCase() === "a") {
            (el as HTMLAnchorElement).click = anchorClickMock as any;
        }

        return el;
    });
});

afterEach(() => {
    vi.unstubAllGlobals();
});

test("1) guard: without router state it shows warning + back button navigates to /", async () => {
    const user = userEvent.setup();

    renderFinalPage({ pathname: "/final" });

    expect(screen.getByText(/no stack data available/i)).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /back to questionnaire/i }));

    const loc = JSON.parse(screen.getByTestId("location").textContent || "{}");
    expect(loc.pathname).toBe("/");
});

test("2) scope rendering: FULL_STACK shows Backend+Frontend+Database rows", async () => {
    const state = {
        result: { architectureScope: "FULL_STACK" },
        personalStack: {
            backend: rec(1, "Spring Boot"),
            frontend: rec(2, "React"),
            database: rec(3, "PostgreSQL"),
            mobile: null,
        },
        draftLink: "http://localhost:3000/draft/abc",
        draftId: "abc",
    };

    renderFinalPage({ pathname: "/final", state });

    expect(screen.getByText(/your final stack/i)).toBeInTheDocument();
    expect(screen.getByText(/full stack/i)).toBeInTheDocument();

    expect(screen.getByText(/^Backend$/)).toBeInTheDocument();
    expect(screen.getByText(/^Frontend$/)).toBeInTheDocument();
    expect(screen.getByText(/^Database$/)).toBeInTheDocument();

    expect(screen.getByText("Spring Boot")).toBeInTheDocument();
    expect(screen.getByText("React")).toBeInTheDocument();
    expect(screen.getByText("PostgreSQL")).toBeInTheDocument();

    expect(screen.queryByText(/^Mobile$/)).not.toBeInTheDocument();

    expect(screen.getByText(/draft link included in pdf/i)).toBeInTheDocument();
    expect(screen.getByRole("link", { name: state.draftLink })).toBeInTheDocument();
});

test("3) dialog open/close: Download PDF opens dialog; Cancel closes it", async () => {
    const user = userEvent.setup();

    const state = {
        result: { architectureScope: "BACKEND_ONLY" },
        personalStack: { backend: rec(1, "Spring Boot"), frontend: null, database: null, mobile: null },
    };

    renderFinalPage({ pathname: "/final", state });

    await user.click(screen.getByRole("button", { name: /download pdf/i }));
    expect(screen.getByText(/export pdf/i)).toBeInTheDocument();

    await user.click(screen.getByRole("button", { name: /cancel/i }));
    await waitFor(() => {
        expect(screen.queryByText(/export pdf/i)).not.toBeInTheDocument();
    });
});

test("4) validation: empty author name shows dialog error and does not call fetch", async () => {
    const user = userEvent.setup();

    const state = {
        result: { architectureScope: "BACKEND_ONLY" },
        personalStack: { backend: rec(1, "Spring Boot"), frontend: null, database: null, mobile: null },
    };

    renderFinalPage({ pathname: "/final", state });

    await user.click(screen.getByRole("button", { name: /download pdf/i }));
    await user.click(screen.getByRole("button", { name: /generate pdf/i }));

    expect(screen.getByText(/please enter your name/i)).toBeInTheDocument();
    expect(globalThis.fetch).not.toHaveBeenCalled();
});

test("5) success: generate calls /api/stack/pdf with correct payload and closes dialog", async () => {
    const user = userEvent.setup();

    (globalThis.fetch as any).mockResolvedValue({
        ok: true,
        status: 200,
        headers: { get: () => 'attachment; filename="file.pdf"' },
        blob: async () => new Blob(["%PDF-1.4"], { type: "application/pdf" }),
    });

    const state = {
        result: { architectureScope: "FULL_STACK" },
        personalStack: {
            backend: rec(1, "Spring Boot"),
            frontend: rec(2, "React"),
            database: rec(3, "PostgreSQL"),
            mobile: null,
        },
        draftLink: "http://localhost:3000/draft/xyz",
        draftId: "xyz",
    };

    renderFinalPage({ pathname: "/final", state });

    await user.click(screen.getByRole("button", { name: /download pdf/i }));
    expect(screen.getByText(/export pdf/i)).toBeInTheDocument();

    await user.type(screen.getByLabelText(/your name \(required\)/i), "  Max  ");
    await user.type(screen.getByLabelText(/organization/i), "  Uni  ");
    await user.type(screen.getByLabelText(/notes/i), "  hello  ");

    await user.click(screen.getByRole("button", { name: /generate pdf/i }));

    await waitFor(() => expect(globalThis.fetch).toHaveBeenCalledTimes(1));

    const [url, opts] = (globalThis.fetch as any).mock.calls[0];
    expect(url).toBe("/api/stack/pdf");
    expect(opts.method).toBe("POST");
    expect(opts.headers["Content-Type"]).toBe("application/json");

    const payload = JSON.parse(opts.body);
    expect(payload).toMatchObject({
        architectureScope: "FULL_STACK",
        backendId: 1,
        frontendId: 2,
        databaseId: 3,
        draftLink: "http://localhost:3000/draft/xyz",
        draftId: "xyz",
        authorName: "Max",
        organization: "Uni",
        notes: "hello",
    });

    expect((URL as any).createObjectURL).toHaveBeenCalled();
    expect(anchorClickMock).toHaveBeenCalled();

    await waitFor(() => {
        expect(screen.queryByText(/export pdf/i)).not.toBeInTheDocument();
    });
});

test("6) failure: backend error shows pdfError alert", async () => {
    const user = userEvent.setup();

    (globalThis.fetch as any).mockResolvedValue({
        ok: false,
        status: 500,
        headers: { get: () => null },
        blob: async () => new Blob([]),
    });

    const state = {
        result: { architectureScope: "BACKEND_ONLY" },
        personalStack: { backend: rec(1, "Spring Boot"), frontend: null, database: null, mobile: null },
    };

    renderFinalPage({ pathname: "/final", state });

    await user.click(screen.getByRole("button", { name: /download pdf/i }));
    await user.type(screen.getByLabelText(/your name \(required\)/i), "Max");
    await user.click(screen.getByRole("button", { name: /generate pdf/i }));

    await waitFor(() => {
        expect(screen.getByText(/pdf generation failed: 500/i)).toBeInTheDocument();
    });
});
