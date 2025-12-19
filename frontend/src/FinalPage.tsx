import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";
import type { QuestionnaireResponse, Recommendation } from "./types/QuestionnaireResponse";
import type { FinalStackRequest } from "./types/FinalStackRequest";

type PersonalStack = {
    backend: Recommendation | null;
    frontend: Recommendation | null;
    database: Recommendation | null;
    mobile: Recommendation | null;
};

type LocationState = {
    result: QuestionnaireResponse;
    personalStack: PersonalStack;
    draftLink?: string | null;
    draftId?: string | null;
};

function FinalStackPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const state = location.state as LocationState | null;

    if (!state) {
        return (
            <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
                <h1>Final Stack</h1>
                <p>No stack data available. Please fill out the questionnaire again.</p>
                <button onClick={() => navigate("/")}>Back to questionnaire</button>
            </div>
        );
    }

    const { result, personalStack } = state;

    const renderItem = (
        label: string,
        rec: Recommendation | null,
        architectureScope: string,
        visibleForScopes: string[]
    ) => {
        if (!visibleForScopes.includes(architectureScope)) {
            return null;
        }

        return (
            <li>
                <strong>{label}:</strong>{" "}
                {rec ? rec.technology.name : "Not selected"}
            </li>
        );
    };

    const [pdfError, setPdfError] = useState<string | null>(null);
    const [pdfLoading, setPdfLoading] = useState(false);

    const downloadPdf = async () => {
        // Build payload from your personalStack
        const payload: FinalStackRequest = {
            architectureScope: result.architectureScope,
            backendId: personalStack.backend?.technology.id ?? undefined,
            frontendId: personalStack.frontend?.technology.id ?? undefined,
            databaseId: personalStack.database?.technology.id ?? undefined,
            mobileId: personalStack.mobile?.technology.id ?? undefined,
            draftLink: state.draftLink ?? undefined,
            draftId: state.draftId ?? undefined,
        };  

        const res = await fetch("/api/stack/pdf", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        if (!res.ok) {
            throw new Error(`PDF generation failed: ${res.status}`);
        }

        // Get filename from header (optional)
        const disposition = res.headers.get("Content-Disposition");
        const fileName =
            disposition?.match(/filename="(.+)"/)?.[1] ?? "archadvisor-stack.pdf";

        const blob = await res.blob();
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement("a");
        a.href = url;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        a.remove();

        window.URL.revokeObjectURL(url);
    };

      const downloadPdfDebug = async () => {
        // Build payload from your personalStack
        console.log("Debug: draftLink =", state.draftLink);
        const payload: FinalStackRequest = {
            architectureScope: result.architectureScope,
            backendId: personalStack.backend?.technology.id ?? undefined,
            frontendId: personalStack.frontend?.technology.id ?? undefined,
            databaseId: personalStack.database?.technology.id ?? undefined,
            mobileId: personalStack.mobile?.technology.id ?? undefined,
            draftLink: state.draftLink ?? undefined,
            draftId: state.draftId ?? undefined,
        };  

        const res = await fetch("/api/stack/pdf", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload),
        });

        if (!res.ok) {
            throw new Error(`PDF generation failed: ${res.status}`);
        }
        console.log("PDF generation response:", res);
    };

    return (
        <div style={{ maxWidth: 800, margin: "2rem auto", fontFamily: "sans-serif" }}>
            <h1>Your final stack</h1>
            <p>
                <strong>Scope:</strong> {result.architectureScope}
            </p>

            <ul>
                {renderItem("Backend", personalStack.backend, result.architectureScope, [
                    "BACKEND_ONLY",
                    "FULL_STACK"
                ])}

                {renderItem("Frontend", personalStack.frontend, result.architectureScope, [
                    "FULL_STACK",
                ])}

                {renderItem("Database", personalStack.database, result.architectureScope, [
                    "FULL_STACK",
                ])}

                {renderItem("Mobile", personalStack.mobile, result.architectureScope, [
                    "MOBILE",
                ])}
            </ul>


            <button
                onClick={async () => {
                    try {
                        setPdfLoading(true);
                        setPdfError(null);
                        await downloadPdf();
                    } catch (e: any) {
                        setPdfError(e?.message ?? "Could not generate PDF");
                    } finally {
                        setPdfLoading(false);
                    }
                }}
                disabled={pdfLoading}
            >
                {pdfLoading ? "Generating PDF..." : "Download PDF"}
            </button>

            {pdfError && <p style={{ color: "red" }}>{pdfError}</p>}
            <button style={{ marginTop: "2rem" }} onClick={() => navigate("/")}>
                Start over
            </button>

            <button style={{ marginTop: "1rem", marginLeft: "1rem" }} onClick={() => downloadPdfDebug()}>
                Debug REST call
            </button>
        </div>
    );
}

export default FinalStackPage;
