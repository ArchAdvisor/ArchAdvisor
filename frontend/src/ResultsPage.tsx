import { useLocation, useNavigate } from "react-router-dom";
import type { QuestionnaireResponse, Recommendation } from "./types/QuestionnaireResponse";
import { useState, useMemo } from "react";
import type { PersonalStack, StackCategory } from "./types/PersonalStack";

import {
  Alert,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Chip,
  Divider,
  Grid,
  Link,
  Paper,
  Stack,
  Typography,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import OpenInNewIcon from "@mui/icons-material/OpenInNew";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import RestartAltIcon from "@mui/icons-material/RestartAlt";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import { StarRateTwoTone } from "@mui/icons-material";

type LocationState = {
  result: QuestionnaireResponse;
  draftLink?: string | null;
  draftId?: string | null;
};

function ResultsPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const state = location.state as LocationState | null;
  const draftLink = state?.draftLink;
  if (!state || !state.result) {
    return (
      <Stack spacing={2}>
        <Typography variant="h5" sx={{ fontWeight: 900 }}>
          Results
        </Typography>
        <Alert severity="warning">
          No results available. Please fill out the questionnaire first.
        </Alert>
        <Box>
          <Button variant="contained" onClick={() => navigate("/")}>
            Back to questionnaire
          </Button>
        </Box>
      </Stack>
    );
  }

  const { result } = state;
  const [personalStack, setPersonalStack] = useState<PersonalStack>({ backend: null, frontend: null, database: null, mobile: null });
  const handleAddToStack = (category: StackCategory, rec: Recommendation) => {
    setPersonalStack(prev => ({
      ...prev,
      [category]: rec,
    }));
  }
  const [warning, setWarning] = useState<string | null>(null);
  const handleGoToFinal = () => {
    const missing: string[] = [];

    switch (result.architectureScope) {
      case "BACKEND_ONLY":
        if (!personalStack.backend) missing.push("Backend");
        break;

      case "FULL_STACK":
        if (!personalStack.backend) missing.push("Backend");
        if (!personalStack.frontend) missing.push("Frontend");
        if (!personalStack.database) missing.push("Database");
        break;

      case "MOBILE":
        if (!personalStack.mobile) missing.push("Mobile");
        break;
    }

    if (missing.length > 0) {
      setWarning(`Please add: ${missing.join(", ")}`);
      return;
    }

    setWarning(null);
    navigate("/final", {
      state: {
        result,
        personalStack,
        draftLink,
        draftId: state.draftId,
      },
    });
  };

  const scopeLabel = useMemo(() => {
    if (result.architectureScope === "BACKEND_ONLY") return "Backend only";
    if (result.architectureScope === "FULL_STACK") return "Full stack";
    if (result.architectureScope === "MOBILE") return "Mobile";
    return result.architectureScope;
  }, [result.architectureScope]);

  const renderSection = (title: string, items: QuestionnaireResponse["backends"],
    category: StackCategory) => {
    if (!items || items.length === 0) return null;
    return (
      <Accordion defaultExpanded sx={{ mb: 2 }}>
        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
          <Stack direction="row" alignItems="center" spacing={1}>
            <Typography variant="h6" sx={{ fontWeight: 900 }}>
              {title}
            </Typography>
            <Chip label={`${items.length}`} size="small" variant="outlined" />
          </Stack>
        </AccordionSummary>

        <AccordionDetails>
          <Grid container spacing={2}>
            {items.map((rec, idx) => {
              const isSelected = personalStack[category]?.technology.id === rec.technology.id;

              return (
                <Grid size={{ xs: 12, md: 6 }} key={rec.technology.id ?? idx}>
                  <Card
                    variant="outlined"
                    sx={{
                      height: "100%",
                      borderColor: isSelected ? "primary.main" : "divider",
                      bgcolor: isSelected ? "rgba(37, 99, 235, 0.06)" : "background.paper",
                    }}
                  >
                    <CardContent sx={{ pb: 1.5 }}>
                      <Stack direction="row" alignItems="flex-start" justifyContent="space-between">
                        <Box>
                          <Typography variant="h6" sx={{ fontWeight: 900, lineHeight: 1.2 }}>
                            {rec.technology.name}
                          </Typography>
                          <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                            Total score: <b>{rec.score.toFixed(2)}</b>
                          </Typography>
                        </Box>

                        {isSelected ? (
                          <Chip
                            icon={<CheckCircleIcon />}
                            label="Selected"
                            color="primary"
                            sx={{ fontWeight: 800 }}
                          />
                        ) : (
                          <Chip
                            icon={<AddCircleOutlineIcon />}
                            label="Not selected"
                            variant="outlined"
                            sx={{ fontWeight: 800 }}
                          />
                        )}
                      </Stack>

                      {rec.warnings && rec.warnings.length > 0 && (
                        <Box sx={{ mt: 1.5 }}>
                          <Typography variant="subtitle2" sx={{ fontWeight: 800, mb: 0.5 }}>
                            Notes
                          </Typography>
                          <Stack spacing={0.5}>
                            {rec.warnings.map((w, i) => (
                              <Typography key={i} variant="body2" color="text.secondary">
                                • {w}
                              </Typography>
                            ))}
                          </Stack>
                        </Box>
                      )}

                      <Stack direction="row" spacing={1} sx={{ mt: 2, flexWrap: "wrap" }}>
                        {rec.technology.githubUrl && (
                          <Link
                            href={rec.technology.githubUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            underline="hover"
                            sx={{ display: "inline-flex", alignItems: "center", gap: 0.5 }}
                          >
                            GitHub <OpenInNewIcon fontSize="inherit" />
                          </Link>
                        )}
                        {rec.technology.documentationUrl && (
                          <Link
                            href={rec.technology.documentationUrl}
                            target="_blank"
                            rel="noopener noreferrer"
                            underline="hover"
                            sx={{ display: "inline-flex", alignItems: "center", gap: 0.5 }}
                          >
                            Documentation <OpenInNewIcon fontSize="inherit" />
                          </Link>
                        )}
                      </Stack>
                    </CardContent>

                    <CardActions sx={{ px: 2, pb: 2, pt: 0 }}>
                      <Button
                        aria-label={`select-${category}-${rec.technology.id}`}
                        type="button"
                        variant={isSelected ? "outlined" : "contained"}
                        disabled={isSelected}
                        onClick={() => handleAddToStack(category, rec)}
                        fullWidth
                      >
                        {isSelected ? "Selected" : "Select"}
                      </Button>
                    </CardActions>
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        </AccordionDetails>
      </Accordion>
    );
  };
  return (
    <Stack spacing={2}>
      <Stack direction={{ xs: "column", sm: "row" }} spacing={1} alignItems={{ sm: "center" }}>
        <Typography variant="h5" sx={{ fontWeight: 900, flexGrow: 1 }}>
          Recommended architecture
        </Typography>
        <Chip label={scopeLabel} color="primary" variant="outlined" sx={{ fontWeight: 800 }} />
      </Stack>

      {/* Recommendations sections */}
      {result.architectureScope === "BACKEND_ONLY" && (
        <>
          {renderSection("Backend frameworks", result.backends, "backend")}
          {renderSection("Databases", result.databases, "database")}
        </>
      )}

      {result.architectureScope === "FULL_STACK" && (
        <>
          {renderSection("Backend frameworks", result.backends, "backend")}
          {renderSection("Frontend frameworks", result.frontends, "frontend")}
          {renderSection("Databases", result.databases, "database")}
        </>
      )}

      {result.architectureScope === "MOBILE" && (
        <>{renderSection("Mobile frameworks", result.mobileFrameworks, "mobile")}</>
      )}

      {/* Selected stack summary (sticky-like feel using Paper + spacing) */}
      <Paper data-testid="selected-stack-panel" variant="outlined" sx={{ p: 2.5 }}>
        <Typography variant="h6" sx={{ fontWeight: 900, mb: 1 }}>
          Your selected stack
        </Typography>

        <Stack spacing={1}>
          {result.architectureScope !== "MOBILE" && (
            <Row label="Backend" value={personalStack.backend?.technology.name} />
          )}
          {result.architectureScope === "FULL_STACK" && (
            <Row label="Frontend" value={personalStack.frontend?.technology.name} />
          )}
          {result.architectureScope === "FULL_STACK" && (
            <Row label="Database" value={personalStack.database?.technology.name} />
          )}
          {result.architectureScope === "MOBILE" && (
            <Row label="Mobile" value={personalStack.mobile?.technology.name} />
          )}
        </Stack>

        <Divider sx={{ my: 2 }} />

        {warning && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {warning}
          </Alert>
        )}

        <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
          <Button
            variant="contained"
            size="large"
            onClick={handleGoToFinal}
            endIcon={<ArrowForwardIcon />}
            sx={{ flex: 1 }}
          >
            Continue to final stack
          </Button>
          <Button
            variant="outlined"
            size="large"
            onClick={() => navigate("/")}
            startIcon={<RestartAltIcon />}
          >
            Start over
          </Button>
        </Stack>
      </Paper>
    </Stack>
  );
}

function Row({ label, value }: { label: string; value?: string }) {
  const hasValue = Boolean(value);
  return (
    <Stack direction="row" spacing={1} alignItems="center" justifyContent="space-between">
      <Typography variant="body2" color="text.secondary">
        {label}
      </Typography>
      {hasValue ? (
        <Chip label={value} color="primary" variant="outlined" sx={{ fontWeight: 800 }} />
      ) : (
        <Chip label="None selected" variant="outlined" />
      )}
    </Stack>
  );
}

export default ResultsPage;
