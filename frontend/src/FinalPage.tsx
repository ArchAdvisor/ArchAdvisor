import { useLocation, useNavigate } from "react-router-dom";
import { useMemo, useState } from "react";
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

type ExportDialogData = {
    authorName: string;
    organization?: string;
    notes?: string;
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

    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [exportData, setExportData] = useState<ExportDialogData>({
        authorName: "",
        organization: "",
        notes: "",
    });
    const [dialogError, setDialogError] = useState<string | null>(null);
    const basePayload = useMemo(
        () => ({
            architectureScope: result.architectureScope,
            backendId: personalStack.backend?.technology.id ?? undefined,
            frontendId: personalStack.frontend?.technology.id ?? undefined,
            databaseId: personalStack.database?.technology.id ?? undefined,
            mobileId: personalStack.mobile?.technology.id ?? undefined,
            draftLink: state.draftLink ?? undefined,
            draftId: state.draftId ?? undefined,
        }),
        [personalStack, result.architectureScope, state.draftId, state.draftLink]
    );

    const downloadPdf = async (payload: FinalStackRequest) => {
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

    const onClickDownload = () => {
        setDialogError(null);
        setIsDialogOpen(true);
    };


    const onConfirmExport = async () => {
        if (!exportData.authorName.trim()) {
            setDialogError("Please enter your name.");
            return;
        }

        try {
            setPdfLoading(true);
            setPdfError(null);

            const payload: FinalStackRequest = {
                ...basePayload,
                authorName: exportData.authorName.trim(),
                organization: exportData.organization?.trim(),
                notes: exportData.notes?.trim(),

            };

            await downloadPdf(payload);

            setIsDialogOpen(false);
        } catch (e: any) {
            setPdfError(e?.message ?? "Could not generate PDF");
        } finally {
            setPdfLoading(false);
        }
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
                    "FULL_STACK",
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

            <button onClick={onClickDownload} disabled={pdfLoading}>
                {pdfLoading ? "Generating PDF..." : "Download PDF"}
            </button>

            {pdfError && <p style={{ color: "red" }}>{pdfError}</p>}

            <button style={{ marginTop: "2rem" }} onClick={() => navigate("/")}>
                Start over
            </button>

            {/* -------- Modal dialog -------- */}
            {isDialogOpen && (
                <div
                    role="dialog"
                    aria-modal="true"
                    style={{
                        position: "fixed",
                        inset: 0,
                        background: "rgba(0,0,0,0.4)",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        padding: "1rem",
                    }}
                    onClick={() => {
                        if (!pdfLoading) setIsDialogOpen(false);
                    }}
                >
                    <div
                        style={{
                            background: "white",
                            width: "min(520px, 100%)",
                            borderRadius: 10,
                            padding: "1rem",
                            boxShadow: "0 10px 30px rgba(0,0,0,0.2)",
                        }}
                        onClick={(e) => e.stopPropagation()}
                    >
                        <h2 style={{ marginTop: 0 }}>Export PDF</h2>

                        <div style={{ marginBottom: "0.75rem" }}>
                            <label style={{ display: "block", fontWeight: 600 }}>
                                Your name (required)
                            </label>
                            <input
                                type="text"
                                value={exportData.authorName}
                                onChange={(e) =>
                                    setExportData((p) => ({ ...p, authorName: e.target.value }))
                                }
                                style={{ width: "100%" }}
                            />
                        </div>

                        <div style={{ marginBottom: "0.75rem" }}>
                            <label style={{ display: "block", fontWeight: 600 }}>
                                Organization (optional)
                            </label>
                            <input
                                type="text"
                                value={exportData.organization}
                                onChange={(e) =>
                                    setExportData((p) => ({ ...p, organization: e.target.value }))
                                }
                                style={{ width: "100%" }}
                            />
                        </div>

                        <div style={{ marginBottom: "0.75rem" }}>
                            <label style={{ display: "block", fontWeight: 600 }}>
                                Notes (optional)
                            </label>
                            <textarea
                                value={exportData.notes}
                                onChange={(e) =>
                                    setExportData((p) => ({ ...p, notes: e.target.value }))
                                }
                                rows={4}
                                style={{ width: "100%" }}
                            />
                        </div>

                        {state.draftLink && (
                            <div style={{ fontSize: "0.9rem", marginBottom: "0.75rem" }}>
                                Draft link that will be included in the PDF:{" "}
                                <a href={state.draftLink}>{state.draftLink}</a>
                            </div>
                        )}

                        {dialogError && <p style={{ color: "red" }}>{dialogError}</p>}

                        <div style={{ display: "flex", justifyContent: "flex-end", gap: "0.5rem" }}>
                            <button
                                type="button"
                                onClick={() => setIsDialogOpen(false)}
                                disabled={pdfLoading}
                            >
                                Cancel
                            </button>
                            <button type="button" onClick={onConfirmExport} disabled={pdfLoading}>
                                {pdfLoading ? "Generating..." : "Generate PDF"}
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default FinalStackPage;
