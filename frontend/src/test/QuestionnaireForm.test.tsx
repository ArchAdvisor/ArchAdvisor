import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { MemoryRouter, Route, Routes, useLocation } from "react-router-dom";
import QuestionnaireForm from "../QuestionnaireForm";
import { expect, test, vi, afterEach } from "vitest";

function renderAt(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/" element={<QuestionnaireForm />} />
        <Route path="/draft/:draftId" element={<QuestionnaireForm />} />
      </Routes>
    </MemoryRouter>
  );
}

function ResultsStateSpy() {
  const loc = useLocation();
  return (
    <pre data-testid="results-state">{JSON.stringify(loc.state ?? null)}</pre>
  );
}

afterEach(() => {
  vi.restoreAllMocks();
});

function mockFetchSequence(handlers: Array<(url: string, init?: RequestInit) => any>) {
  const fetchMock = vi
    .spyOn(globalThis, "fetch")
    .mockImplementation(async (input: any, init?: any) => {
      const url = typeof input === "string" ? input : input?.url;
      const handler = handlers.shift();
      if (!handler) throw new Error(`Unexpected fetch call: ${url}`);
      return handler(url, init);
    });

  return fetchMock;
}


function okJson(data: any) {
  return Promise.resolve({
    ok: true,
    status: 200,
    json: async () => data,
    headers: new Headers(),
  } as any);
}

test("loads a draft and populates key fields (projectName, deployment, budgetTier, teamSize, topRankN, languages, priority order)", async () => {
  mockFetchSequence([
    (url) => {
      expect(url).toMatch(/\/api\/questionnaire-drafts\/abc$/);
      return okJson({
        projectName: "HDWD",
        architectureScope: "FULL_STACK",
        deploymentPreference: "SERVERLESS",
        budgetTier: "HIGH",
        expectedUsers: 123,
        teamSize: 5,
        experienceLevel: "mixed",
        programmingLanguages: ["PYTHON"],
        priorityAspects: [
          "SCALABILITY",
          "PERFORMANCE",
          "MAINTAINABILITY",
          "SECURITY",
          "COST_EFFECTIVENESS",
          "COMMUNITY_SUPPORT",
          "ECOSYSTEM_MATURITY",
          "VENDOR_LOCKIN_AVOIDANCE",
        ],
        topRankN: 7,
        openSource: true,
        serverlessFriendly: true,
      });
    },
  ]);

  renderAt("/draft/abc");

  expect(await screen.findByLabelText(/name of the project/i)).toHaveValue("HDWD");

  const deploymentSelect = screen.getByLabelText(/deployment preference/i);
  expect(deploymentSelect).toHaveTextContent(/serverless/i);

  const budgetSelect = await screen.findByLabelText(/budget tier/i);
  expect(budgetSelect).toHaveTextContent(/high/i);

  const teamSize = screen.getByLabelText(/team size/i) as HTMLInputElement;
  expect(teamSize).toHaveValue(5);

  const topRank = screen.getByLabelText(/number of recommendations/i) as HTMLInputElement;
  expect(topRank).toHaveValue(7);

  expect(screen.getByText(/selected:/i)).toHaveTextContent(/python/i);

  expect(screen.getByText(/^1\.\s*Scalability$/)).toBeInTheDocument();
});

test("submit sends correct payload (including renamed keys and priority order)", async () => {
  const user = userEvent.setup();

  const fetchMock = mockFetchSequence([
    (url, init) => {
      expect(url).toBe("/api/questionnaire-drafts");
      expect(init?.method).toBe("POST");
      return okJson("new-draft-123");
    },
    (url, init) => {
      expect(url).toBe("/api/questionnaire");
      expect(init?.method).toBe("POST");
      return okJson({
        architectureScope: "BACKEND_ONLY",
        backends: [],
        databases: [],
        frontends: null,
        mobileFrameworks: null,
      });
    },
  ]);

  renderAt("/");

  await user.type(screen.getByLabelText(/name of the project/i), "MyApp");

  await user.click(screen.getByLabelText(/deployment preference/i));
  await user.click(await screen.findByRole("option", { name: /serverless/i }));

  await user.click(await screen.findByLabelText(/budget tier/i));
  await user.click(await screen.findByRole("option", { name: /medium/i }));

  await user.click(screen.getByLabelText(/move down performance/i));
  expect(screen.getByText(/^1\.\s*Scalability$/)).toBeInTheDocument();

  await user.click(screen.getByRole("button", { name: /submit and save as draft/i }));

  await waitFor(() => expect(fetchMock).toHaveBeenCalledTimes(2));

  const secondCall = fetchMock.mock.calls[1];
  const body = JSON.parse(secondCall[1]?.body as string);

  expect(body.projectName).toBe("MyApp");
  expect(body.deploymentPreferences).toBe("SERVERLESS");
  expect(body.budgetTier).toBe("MEDIUM");

  expect(body.priorityAspects[0]).toBe("SCALABILITY");
  expect(body.priorityAspects[1]).toBe("PERFORMANCE");

  expect(body.topRankN).toBe(4);
});

test("shows Budget tier only for cloud-like deployment preferences", async () => {
  renderAt("/");

  expect(screen.queryByLabelText(/budget tier/i)).not.toBeInTheDocument();

  await userEvent.click(screen.getByLabelText(/deployment preference/i));
  await userEvent.click(await screen.findByRole("option", { name: /serverless/i }));

  expect(await screen.findByLabelText(/budget tier/i)).toBeInTheDocument();
});

test("priority ranking: move down swaps order", async () => {
  renderAt("/");

  expect(screen.getByText(/1\.\s*performance/i)).toBeInTheDocument();

  const moveDownButtons = screen.getAllByLabelText(/move down performance/i);
  await userEvent.click(moveDownButtons[0]);
  await waitFor(() => {
    expect(screen.queryByText(/1\.\s*performance/i)).not.toBeInTheDocument();
  });
});

test("submitting creates a draft when no draftId exists", async () => {
  renderAt("/");

  await userEvent.type(screen.getByLabelText(/name of the project/i), "My Project");

  await userEvent.click(screen.getByRole("button", { name: /submit/i }));

  await waitFor(() => {
    expect(screen.queryByText(/error/i)).not.toBeInTheDocument();
  });
});


test("entering inputs updates the form values (basic mapping)", async () => {
  const user = userEvent.setup();

  render(
    <MemoryRouter initialEntries={["/"]}>
      <Routes>
        <Route path="/" element={<QuestionnaireForm />} />
      </Routes>
    </MemoryRouter>
  );

  const projectName = screen.getByLabelText(/name of the project/i);
  await user.type(projectName, "MyApp");
  expect(projectName).toHaveValue("MyApp");

  const teamSize = screen.getByLabelText(/team size/i) as HTMLInputElement;

  await user.click(teamSize);
  await user.clear(teamSize);
  await user.type(teamSize, "3");

  expect(teamSize).toHaveValue(3);

  await user.click(screen.getByRole("button", { name: /python/i }));
  expect(screen.getByText(/selected:/i)).toHaveTextContent("Python");
});

test("number inputs reset to default values on blur if left empty or invalid", async () => {
  const user = userEvent.setup();
  renderAt("/");
  const topRankInput = screen.getByLabelText(/number of recommendations/i);

  await user.clear(topRankInput);
  expect(topRankInput).toHaveValue(null);
  await user.tab();
  expect(topRankInput).toHaveValue(4);
  const teamSizeInput = screen.getByLabelText(/team size/i);
  await user.clear(teamSizeInput);
  await user.tab();
  expect(teamSizeInput).toHaveValue(1);
});
