import { useLocation, useNavigate } from "react-router-dom";
import { useMemo, useState } from "react";
import type { QuestionnaireResponse, Recommendation } from "./types/QuestionnaireResponse";
import type { FinalStackRequest } from "./types/FinalStackRequest";
import {
    Alert,
    Button,
    Chip,
    CircularProgress,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Divider,
    Link,
    Paper,
    Stack,
    TextField,
    Typography,
} from "@mui/material";

import DownloadIcon from "@mui/icons-material/Download";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";

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

    console.log("FinalPage, draftLink:", state?.draftLink);
    if (!state) {
        return (
            <Stack spacing={2}>
                <Typography variant="h5" sx={{ fontWeight: 900 }}>
                    Final stack
                </Typography>
                <Alert severity="warning">
                    No stack data available. Please fill out the questionnaire again.
                </Alert>
                <Button variant="contained" onClick={() => navigate("/")}>
                    Back to questionnaire
                </Button>
            </Stack>
        );
    }

    const { result, personalStack } = state;

    const scopeLabel = useMemo(() => {
        if (result.architectureScope === "BACKEND_ONLY") return "Backend only";
        if (result.architectureScope === "FULL_STACK") return "Full stack";
        if (result.architectureScope === "MOBILE") return "Mobile";
        return result.architectureScope;
    }, [result.architectureScope]);

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

    const renderRow = (
        label: string,
        rec: Recommendation | null,
        visibleForScopes: Array<QuestionnaireResponse["architectureScope"]>
    ) => {
        if (!visibleForScopes.includes(result.architectureScope)) return null;

        const name = rec?.technology.name;
        return (
            <Stack direction="row" alignItems="center" justifyContent="space-between">
                <Typography variant="body2" color="text.secondary">
                    {label}
                </Typography>
                {name ? (
                    <Chip label={name} color="primary" variant="outlined" sx={{ fontWeight: 800 }} />
                ) : (
                    <Chip label="Not selected" variant="outlined" />
                )}
            </Stack>
        );
    };


    return (
        <Stack spacing={2}>
            <Stack direction={{ xs: "column", sm: "row" }} spacing={1} alignItems={{ sm: "center" }}>
                <Typography variant="h5" sx={{ fontWeight: 900, flexGrow: 1 }}>
                    Your final stack
                </Typography>
                <Chip label={scopeLabel} color="primary" variant="outlined" sx={{ fontWeight: 800 }} />
            </Stack>

            <Paper variant="outlined" sx={{ p: 2.5 }}>
                <Typography variant="h6" sx={{ fontWeight: 900, mb: 1 }}>
                    Selected technologies
                </Typography>

                <Stack spacing={1.25}>
                    {renderRow("Backend", personalStack.backend, ["BACKEND_ONLY", "FULL_STACK"])}
                    {renderRow("Frontend", personalStack.frontend, ["FULL_STACK"])}
                    {renderRow("Database", personalStack.database, ["FULL_STACK"])}
                    {renderRow("Mobile", personalStack.mobile, ["MOBILE"])}
                </Stack>

                <Divider sx={{ my: 2 }} />

                <Stack spacing={1}>
                    <Typography variant="body2" color="text.secondary">
                        Export a PDF containing your selected stack and relevant metadata (name, organization,
                        notes).
                    </Typography>

                    {state.draftLink && (
                        <Typography variant="body2" color="text.secondary" sx={{ wordBreak: "break-all" }}>
                            Draft link included in PDF:{" "}
                            <Link href={state.draftLink} target="_blank" rel="noopener noreferrer">
                                {state.draftLink}
                            </Link>
                        </Typography>
                    )}
                </Stack>
            </Paper>

            {pdfError && <Alert severity="error">{pdfError}</Alert>}

            <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
                <Button
                    variant="contained"
                    size="large"
                    startIcon={<DownloadIcon />}
                    onClick={onClickDownload}
                    disabled={pdfLoading}
                    sx={{ flex: 1 }}
                >
                    Download PDF
                </Button>

                <Button
                    variant="outlined"
                    size="large"
                    startIcon={<ArrowBackIcon />}
                    onClick={() => navigate(-1)}
                    disabled={pdfLoading}
                >
                    Back
                </Button>

                <Button
                    variant="outlined"
                    size="large"
                    startIcon={<RestartAltIcon />}
                    onClick={() => navigate("/")}
                    disabled={pdfLoading}
                >
                    Start over
                </Button>
            </Stack>

            {/* MUI Dialog */}
            <Dialog
                open={isDialogOpen}
                onClose={() => {
                    if (!pdfLoading) setIsDialogOpen(false);
                }}
                fullWidth
                maxWidth="sm"
            >
                <DialogTitle sx={{ fontWeight: 900 }}>Export PDF</DialogTitle>

                <DialogContent dividers>
                    <Stack spacing={2}>
                        <TextField
                            label="Your name (required)"
                            value={exportData.authorName}
                            onChange={(e) => setExportData((p) => ({ ...p, authorName: e.target.value }))}
                            fullWidth
                            autoFocus
                        />

                        <TextField
                            label="Organization (optional)"
                            value={exportData.organization}
                            onChange={(e) => setExportData((p) => ({ ...p, organization: e.target.value }))}
                            fullWidth
                        />

                        <TextField
                            label="Notes (optional)"
                            value={exportData.notes}
                            onChange={(e) => setExportData((p) => ({ ...p, notes: e.target.value }))}
                            fullWidth
                            multiline
                            minRows={4}
                        />

                        {state.draftLink && (
                            <Alert severity="info">
                                The PDF will include the draft link:{" "}
                                <Link href={state.draftLink} target="_blank" rel="noopener noreferrer">
                                    {state.draftLink}
                                </Link>
                            </Alert>
                        )}

                        {dialogError && <Alert severity="error">{dialogError}</Alert>}
                    </Stack>
                </DialogContent>

                <DialogActions sx={{ p: 2 }}>
                    <Button onClick={() => setIsDialogOpen(false)} disabled={pdfLoading}>
                        Cancel
                    </Button>

                    <Button
                        variant="contained"
                        onClick={onConfirmExport}
                        disabled={pdfLoading}
                        startIcon={pdfLoading ? <CircularProgress size={18} /> : <DownloadIcon />}
                    >
                        {pdfLoading ? "Generating..." : "Generate PDF"}
                    </Button>
                </DialogActions>
            </Dialog>
        </Stack>
    );
}

export default FinalStackPage;
