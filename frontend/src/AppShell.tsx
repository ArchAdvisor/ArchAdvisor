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
  Stack,
  Chip,
} from "@mui/material";
import Grid from "@mui/material/Grid";

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
      <AppBar
        position="sticky"
        elevation={0}
        color="default"
        sx={{ borderBottom: 1, borderColor: "divider" }}
      >
        <Toolbar sx={{ gap: 1.5 }}>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              gap: 1.25,
              cursor: "pointer",
            }}
            onClick={() => navigate("/")}
          >
            <Box
              component="img"
              src="/logo_01.png"
              alt="ArchAdvisor logo"
              sx={{
                height: 28,
                width: "auto",
                display: "block",
              }}
            />

            <Typography
              variant="h6"
              sx={{
                fontWeight: 900,
                letterSpacing: 0.2,
                lineHeight: 1,
              }}
            >
              ArchAdvisor
            </Typography>
          </Box>

          <Box sx={{ flexGrow: 1 }} />
        </Toolbar>
      </AppBar>

      <Container maxWidth="xl" sx={{ py: { xs: 3, md: 5 } }}>
        <Grid container spacing={3} alignItems="flex-start">
          <Grid size={{ xs: 12, md: 8 }}>
            <Paper sx={{ p: { xs: 2, sm: 3, md: 4 } }}>
              <Typography variant="h4" sx={{ fontWeight: 900, mb: 0.5 }}>
                {title}
              </Typography>
              <Typography variant="body2" color="text.secondary" sx={{ mb: 3 }}>
                Answer a few questions, review recommendations, then export your selected stack to PDF.
              </Typography>

              {!pathname.startsWith("/draft/") && (
                <Stepper activeStep={activeStep} sx={{ mb: 4 }}>
                  {steps.map((s) => (
                    <Step
                      key={s.path}
                      onClick={() => navigate(s.path)}
                      sx={{ cursor: "pointer" }}
                    >
                      <StepLabel>{s.label}</StepLabel>
                    </Step>
                  ))}
                </Stepper>
              )}

              {children}
            </Paper>
          </Grid>

          {/* Right column: info panel (desktop only) */}
          <Grid size={{ xs: 12, md: 4 }} sx={{ display: "block" }}>
            <Paper
              variant="outlined"
              sx={{
                mt: 8,
                p: 2.5,
                position: "sticky",
                top: 88,
              }}
            >
              <Typography variant="subtitle1" sx={{ fontWeight: 900, mb: 1 }}>
                Overview
              </Typography>

              <Stack spacing={1.25}>
                <Typography variant="body2" color="text.secondary">
                  Drafts can be shared and resumed. After submitting once, copy the link from the banner.
                </Typography>

                <Typography variant="body2" color="text.secondary">
                  Recommendations depend mainly on scope, team constraints, and priority ranking.
                </Typography>

                <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap", gap: 1 }}>
                  <Chip label="Drafts" size="small" variant="outlined" />
                  <Chip label="Selection" size="small" variant="outlined" />
                  <Chip label="PDF Export" size="small" variant="outlined" />
                </Stack>
              </Stack>
            </Paper>
          </Grid>
        </Grid>

        <Box sx={{ py: 3, textAlign: "center", color: "text.secondary" }}>
          <Typography variant="body2">© {new Date().getFullYear()} ArchAdvisor</Typography>
        </Box>
      </Container>
    </Box>
  );
}