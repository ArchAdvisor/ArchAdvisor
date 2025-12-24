import { type PropsWithChildren, useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import {
  AppBar,
  Box,
  Container,
  Paper,
  Step,
  StepLabel,
  Stepper,
  Toolbar,
  Typography,
} from "@mui/material";

type StepDef = {
  label: string;
  path: string;
};

const steps: StepDef[] = [
  { label: "Questionnaire", path: "/" },
  { label: "Recommendations", path: "/results" },
  { label: "Export", path: "/final" },
];

function computeActiveStep(pathname: string) {
  if (pathname.startsWith("/results")) return 1;
  if (pathname.startsWith("/final")) return 2;
  // drafts should still count as questionnaire step
  if (pathname.startsWith("/draft/")) return 0;
  return 0;
}

function pageTitle(pathname: string) {
  if (pathname.startsWith("/results")) return "Recommended tech stacks";
  if (pathname.startsWith("/final")) return "Finalize & export";
  if (pathname.startsWith("/draft/")) return "Continue your questionnaire";
  return "Architecture questionnaire";
}

export function AppShell({ children }: PropsWithChildren) {
  const { pathname } = useLocation();
  const navigate = useNavigate();

  const activeStep = useMemo(() => computeActiveStep(pathname), [pathname]);
  const title = useMemo(() => pageTitle(pathname), [pathname]);

  return (
    <Box sx={{ minHeight: "100vh", bgcolor: "background.default" }}>
      <AppBar position="sticky" elevation={0} color="default">
        <Toolbar sx={{ gap: 1.5 }}>
          <Typography
            variant="h6"
            sx={{ fontWeight: 900, letterSpacing: 0.2, cursor: "pointer" }}
            onClick={() => navigate("/")}
          >
            ArchAdvisor
          </Typography>

          <Box sx={{ flexGrow: 1 }} />
        </Toolbar>
      </AppBar>

      <Container maxWidth="lg" sx={{ py: { xs: 3, md: 5 } }}>
        <Paper sx={{ p: { xs: 2, sm: 3, md: 4 } }}>
          <Typography variant="h4" sx={{ fontWeight: 900, mb: 0.5 }}>
            {title}
          </Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
            Answer a few questions, review recommendations, then export your selected stack to PDF.
          </Typography>

          {/* Stepper for the main flow only */}
          {!pathname.startsWith("/draft/") && (
            <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
              {steps.map((s) => (
                <Step key={s.path} onClick={() => navigate(s.path)} sx={{ cursor: "pointer" }}>
                  <StepLabel>{s.label}</StepLabel>
                </Step>
              ))}
            </Stepper>
          )}

          {children}
        </Paper>

        <Box sx={{ py: 3, textAlign: "center", color: "text.secondary" }}>
          <Typography variant="body2">
            © {new Date().getFullYear()} ArchAdvisor
          </Typography>
        </Box>
      </Container>
    </Box>
  );
}