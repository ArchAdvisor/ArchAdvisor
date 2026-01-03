import { type FormEvent, useState, useEffect, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    Alert,
    Box,
    Button,
    Chip,
    CircularProgress,
    Divider,
    FormControl,
    FormControlLabel,
    InputLabel,
    List,
    ListItem,
    ListItemText,
    MenuItem,
    Paper,
    Select,
    Stack,
    TextField,
    Typography,
    IconButton,
    Tooltip,
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";


const ProgrammingLanguages = {
    JAVASCRIPT: "JAVASCRIPT",
    PYTHON: "PYTHON",
    JAVA: "JAVA",
    CSHARP: "CSHARP",
} as const;

const DeploymentPreferences = {
    SELF_HOSTED: "SELF_HOSTED",
    PAAS: "PAAS",
    CLOUD_NATIVE: "CLOUD_NATIVE",
    SERVERLESS: "SERVERLESS",
    KUBERNETES: "KUBERNETES",
    ON_PREM: "ON_PREM",
    HYBRID: "HYBRID",
} as const;

const BudgetTier = {
    LOW: "LOW",
    MEDIUM: "MEDIUM",
    HIGH: "HIGH",
} as const;

const PriorityAspects = {
    PERFORMANCE: "PERFORMANCE",
    SCALABILITY: "SCALABILITY",
    MAINTAINABILITY: "MAINTAINABILITY",
    SECURITY: "SECURITY",
    COST_EFFECTIVENESS: "COST_EFFECTIVENESS",
    COMMUNITY_SUPPORT: "COMMUNITY_SUPPORT",
    ECOSYSTEM_MATURITY: "ECOSYSTEM_MATURITY",
    VENDOR_LOCKIN_AVOIDANCE: "VENDOR_LOCKIN_AVOIDANCE",
} as const;

const PRIORITY_ASPECT_LABELS: Record<PriorityAspects, string> = {
    [PriorityAspects.PERFORMANCE]: "Performance",
    [PriorityAspects.SCALABILITY]: "Scalability",
    [PriorityAspects.MAINTAINABILITY]: "Maintainability",
    [PriorityAspects.SECURITY]: "Security",
    [PriorityAspects.COST_EFFECTIVENESS]: "Cost-effectiveness",
    [PriorityAspects.COMMUNITY_SUPPORT]: "Community support",
    [PriorityAspects.ECOSYSTEM_MATURITY]: "Ecosystem maturity",
    [PriorityAspects.VENDOR_LOCKIN_AVOIDANCE]: "Vendor lock-in avoidance",
};


type DeploymentPreferences = typeof DeploymentPreferences[keyof typeof DeploymentPreferences];
type ProgrammingLanguages = typeof ProgrammingLanguages[keyof typeof ProgrammingLanguages];
type PriorityAspects = typeof PriorityAspects[keyof typeof PriorityAspects];
type BudgetTier = typeof BudgetTier[keyof typeof BudgetTier];

type QuestionnaireRequest = {
    project_name: string;
    architectureScope: string | null;
    deploymentPreference: DeploymentPreferences | null;
    budgetTier: BudgetTier | null;
    isOpenSource: boolean;
    isServerlessFriendly: boolean
    expectedUsers: number | null;
    teamSize: number;
    experienceLevel: string;
    teamProgrammingLanguages: ProgrammingLanguages[];
    priorityAspects: PriorityAspects[];
    topRankN?: number;
};

type QuestionnaireResponse = {
    architectureScope: string;
    backends: any[] | null;
    frontends: any[] | null;
    databases: any[] | null;
    mobileFrameworks: any[] | null;
};

function QuestionnaireForm() {
    const [form, setForm] = useState<QuestionnaireRequest>({
        project_name: "",
        architectureScope: "BACKEND_ONLY",
        budgetTier: null,
        isOpenSource: false,
        deploymentPreference: null,
        isServerlessFriendly: false,
        expectedUsers: null,
        teamSize: 1,
        experienceLevel: "",
        priorityAspects: [
            PriorityAspects.PERFORMANCE,
            PriorityAspects.SCALABILITY,
            PriorityAspects.MAINTAINABILITY,
            PriorityAspects.SECURITY,
            PriorityAspects.COST_EFFECTIVENESS,
            PriorityAspects.COMMUNITY_SUPPORT,
            PriorityAspects.ECOSYSTEM_MATURITY,
            PriorityAspects.VENDOR_LOCKIN_AVOIDANCE,
        ],
        teamProgrammingLanguages: [],
        topRankN: 4,
    });
    const formDefaults: QuestionnaireRequest = {
        project_name: "",
        architectureScope: "BACKEND_ONLY",
        budgetTier: null,
        isOpenSource: false,
        deploymentPreference: null,
        isServerlessFriendly: false,
        expectedUsers: null,
        teamSize: 1,
        experienceLevel: "",
        priorityAspects: [
            PriorityAspects.PERFORMANCE,
            PriorityAspects.SCALABILITY,
            PriorityAspects.MAINTAINABILITY,
            PriorityAspects.SECURITY,
            PriorityAspects.COST_EFFECTIVENESS,
            PriorityAspects.COMMUNITY_SUPPORT,
            PriorityAspects.ECOSYSTEM_MATURITY,
            PriorityAspects.VENDOR_LOCKIN_AVOIDANCE,
        ],
        teamProgrammingLanguages: [],
        topRankN: 4,
    };

    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState<QuestionnaireResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    const toggleLanguage = (lang: ProgrammingLanguages) => {
        setForm(prev => {
            const selected = prev.teamProgrammingLanguages;
            return selected.includes(lang)
                ? { ...prev, teamProgrammingLanguages: selected.filter(l => l !== lang) }
                : { ...prev, teamProgrammingLanguages: [...selected, lang] };
        });
    }
    const moveAspect = (index: number, direction: -1 | 1) => {
        setForm(prev => {
            const arr = [...prev.priorityAspects];
            const newIndex = index + direction;

            if (newIndex < 0 || newIndex >= arr.length) {
                return prev; // out of bounds, no change
            }

            // swap positions
            const temp = arr[index];
            arr[index] = arr[newIndex];
            arr[newIndex] = temp;

            return { ...prev, priorityAspects: arr };
        });
    };

    const navigate = useNavigate();
    const { draftId } = useParams<{ draftId: string }>();
    const [effectiveDraftId, setEffectiveDraftId] = useState<string | null>(draftId ?? null);

    useEffect(() => {
        if (draftId) setEffectiveDraftId(draftId);
    }, [draftId]);
    // base URL for printing / sharing
    const draftLink = useMemo(() => {
        if (!effectiveDraftId) return null;
        return `${window.location.origin}/draft/${effectiveDraftId}`;
    }, [effectiveDraftId]);

    useEffect(() => {
        if (!draftId) return;

        (async () => {
            try {
                setError(null);
                setLoading(true);

                const res = await fetch(`/api/questionnaire-drafts/${draftId}`);
                if (!res.ok) {
                    throw new Error(`Failed to load draft (${res.status})`);
                }

                const dto = await res.json();

                // map backend DTO -> frontend state keys
                // backend: deploymentPreferences, expectedNumberOfUsers, programmingLanguages
                // frontend: deploymentPreference, expectedUsers, teamProgrammingLanguages
                const loaded: QuestionnaireRequest = {
                    ...formDefaults,
                    architectureScope: dto.architectureScope ?? formDefaults.architectureScope,
                    deploymentPreference: dto.deploymentPreference ?? dto.deploymentPreferences ?? null,
                    budgetTier: dto.budgetTier ?? null,
                    isOpenSource: dto.openSource ?? dto.isOpenSource ?? false,
                    isServerlessFriendly: dto.serverlessFriendly ?? dto.isServerlessFriendly ?? false,
                    expectedUsers: dto.expectedUsers ?? dto.expectedNumberOfUsers ?? null,
                    teamSize: dto.teamSize ?? 0,
                    experienceLevel: dto.experienceLevel ?? "",
                    teamProgrammingLanguages: dto.teamProgrammingLanguages ?? dto.programmingLanguages ?? [],
                    priorityAspects: dto.priorityAspects ?? formDefaults.priorityAspects,
                    topRankN: dto.topRankN ?? formDefaults.topRankN,
                    project_name: dto.projectName ?? "",
                };

                setForm(loaded);
            } catch (e: any) {
                setError(e?.message ?? "Could not load draft");
            } finally {
                setLoading(false);
            }
        })();
    }, [draftId]);

    const saveDraft = async (): Promise<string> => {
        const payload = {
            projectName: form.project_name,
            architectureScope: form.architectureScope,
            isOpenSource: form.isOpenSource,
            deploymentPreference: form.deploymentPreference,
            budgetTier: form.budgetTier,
            expectedUsers: form.expectedUsers,
            teamSize: form.teamSize,
            serverlessFriendly: form.isServerlessFriendly,
            experienceLevel: form.experienceLevel,
            programmingLanguages: form.teamProgrammingLanguages,
            priorityAspects: form.priorityAspects,
            topRankN: form.topRankN,
        };

        if (draftId) {
            // update
            const res = await fetch(`/api/questionnaire-drafts/${draftId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                throw new Error(`Failed to update draft (${res.status})`);
            }

            return draftId;
        } else {
            // create
            const res = await fetch("/api/questionnaire-drafts", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                throw new Error(`Failed to create draft (${res.status})`);
            }

            // backend returns JSON string uuid: "...."
            const newId: string = await res.json();
            return newId;
        }
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setResult(null);

        try {
            const savedId = await saveDraft();
            setEffectiveDraftId(savedId);
            // if it was created, move user to /draft/:id so they have the shareable link
            if (!draftId) {
                navigate(`/draft/${savedId}`, { replace: true });
            }
            const link = `${window.location.origin}/draft/${savedId}`;
            var body = JSON.stringify({
                projectName: form.project_name,
                architectureScope: form.architectureScope,
                isOpenSource: form.isOpenSource,
                deploymentPreferences: form.deploymentPreference,
                budgetTier: form.budgetTier,
                expectedNumberOfUsers: form.expectedUsers,
                teamSize: form.teamSize,
                isServerlessFriendly: form.isServerlessFriendly,
                experienceLevel: form.experienceLevel,
                programmingLanguages: form.teamProgrammingLanguages,
                priorityAspects: form.priorityAspects,
                topRankN: form.topRankN,
            });
            const response = await fetch("/api/questionnaire", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: body,
            });

            if (!response.ok) {
                throw new Error(`Backend returned status ${response.status}`);
            }

            const data: QuestionnaireResponse = await response.json();
            navigate("/results", { state: { result: data, draftLink: link, draftId: savedId } });
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Unknown error");
        } finally {
            setLoading(false);
        }
    };

    const selectedLanguageLabels = useMemo(() => {
        const map: Record<ProgrammingLanguages, string> = {
            [ProgrammingLanguages.JAVASCRIPT]: "JavaScript",
            [ProgrammingLanguages.PYTHON]: "Python",
            [ProgrammingLanguages.JAVA]: "Java",
            [ProgrammingLanguages.CSHARP]: "C#",
        };
        return form.teamProgrammingLanguages.map((l) => map[l]);
    }, [form.teamProgrammingLanguages]);

    return (
        <Box>
            {draftLink && (
                <Alert
                    severity="info"
                    sx={{ mb: 3 }}
                    action={
                        <Tooltip title="Copy link">
                            <IconButton
                                color="inherit"
                                size="small"
                                onClick={() => navigator.clipboard.writeText(draftLink)}
                            >
                                <ContentCopyIcon fontSize="small" />
                            </IconButton>
                        </Tooltip>
                    }
                >
                    <Typography variant="body2" sx={{ fontWeight: 700 }}>
                        Editing draft
                    </Typography>
                    <Typography variant="body2" sx={{ wordBreak: "break-all" }}>
                        {draftLink}
                    </Typography>
                </Alert>
            )}

            <form onSubmit={handleSubmit}>
                <Stack spacing={3}>
                    {/* Project name */}
                    <TextField
                        label="Name of the project"
                        value={form.project_name}
                        onChange={(e) => setForm({ ...form, project_name: e.target.value })}
                        fullWidth
                    />

                    {/* Scope */}
                    <FormControl fullWidth>
                        <InputLabel id="scope-label">Architecture scope</InputLabel>
                        <Select
                            labelId="scope-label"
                            label="Architecture scope"
                            value={form.architectureScope ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    architectureScope: e.target.value === "" ? null : (e.target.value as string),
                                })
                            }
                        >
                            <MenuItem value="">Select an option</MenuItem>
                            <MenuItem value="BACKEND_ONLY">Backend only</MenuItem>
                            <MenuItem value="FULL_STACK">Full stack</MenuItem>
                            <MenuItem value="MOBILE">Mobile</MenuItem>
                        </Select>
                    </FormControl>

                    {/* Open source */}
                    <Paper variant="outlined" sx={{ p: 2 }}>
                        <FormControlLabel
                            control={
                                <input
                                    type="checkbox"
                                    checked={form.isOpenSource}
                                    onChange={(e) => setForm({ ...form, isOpenSource: e.target.checked })}
                                    style={{ accentColor: "currentColor" }}
                                />
                            }
                            label="Only propose open-source frameworks"
                        />
                        <Typography variant="body2" color="text.secondary">
                            Restricts recommendations to open-source options when possible.
                        </Typography>
                    </Paper>

                    {/* Deployment preference */}
                    <FormControl fullWidth>
                        <InputLabel id="deploy-label">Deployment preference</InputLabel>
                        <Select
                            labelId="deploy-label"
                            label="Deployment preference"
                            value={form.deploymentPreference ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    deploymentPreference:
                                        (e.target.value as DeploymentPreferences),
                                })
                            }
                        >
                            <MenuItem value="">Select an option</MenuItem>
                            <MenuItem value={DeploymentPreferences.SELF_HOSTED}>Self-hosted</MenuItem>
                            <MenuItem value={DeploymentPreferences.PAAS}>Platform as a Service (PaaS)</MenuItem>
                            <MenuItem value={DeploymentPreferences.CLOUD_NATIVE}>Cloud-native</MenuItem>
                            <MenuItem value={DeploymentPreferences.SERVERLESS}>Serverless</MenuItem>
                            <MenuItem value={DeploymentPreferences.KUBERNETES}>Kubernetes</MenuItem>
                            <MenuItem value={DeploymentPreferences.ON_PREM}>On-premises</MenuItem>
                            <MenuItem value={DeploymentPreferences.HYBRID}>Hybrid</MenuItem>
                        </Select>
                    </FormControl>

                    {/* Budget tier */}
                    <FormControl fullWidth>
                        <InputLabel id="budget-label">Budget tier</InputLabel>
                        <Select
                            labelId="budget-label"
                            label="Budget tier"
                            value={form.budgetTier ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    budgetTier: (e.target.value as BudgetTier),
                                })
                            }
                        >
                            <MenuItem value="">Select an option</MenuItem>
                            <MenuItem value={BudgetTier.LOW}>Low</MenuItem>
                            <MenuItem value={BudgetTier.MEDIUM}>Medium</MenuItem>
                            <MenuItem value={BudgetTier.HIGH}>High</MenuItem>
                        </Select>
                    </FormControl>

                    {/* Serverless friendly */}
                    <Paper variant="outlined" sx={{ p: 2 }}>
                        <FormControlLabel
                            control={
                                <input
                                    type="checkbox"
                                    checked={form.isServerlessFriendly}
                                    onChange={(e) =>
                                        setForm({ ...form, isServerlessFriendly: e.target.checked })
                                    }
                                    style={{ accentColor: "currentColor" }}
                                />
                            }
                            label="Prefer serverless-friendly technologies"
                        />
                        <Typography variant="body2" color="text.secondary">
                            Prioritizes options that work well with serverless/managed execution models.
                        </Typography>
                    </Paper>

                    {/* Expected users */}
                    <TextField
                        label="Expected number of users"
                        type="number"
                        value={form.expectedUsers ?? ""}
                        inputProps={{ min: 1 }}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                expectedUsers: e.target.value === "" ? null : Number(e.target.value),
                            })
                        }
                        fullWidth
                    />

                    {/* Team size */}
                    <TextField
                        label="Team size"
                        type="number"
                        value={form.teamSize ?? ""}
                        inputProps={{ min: 1 }}
                        onChange={(e) =>
                            setForm({
                                ...form,
                                teamSize: e.target.value === "" ? 1 : Number(e.target.value),
                            })
                        }
                        fullWidth
                    />

                    {/* Experience level */}
                    <TextField
                        label="Experience level"
                        value={form.experienceLevel}
                        onChange={(e) => setForm({ ...form, experienceLevel: e.target.value })}
                        fullWidth
                        placeholder="e.g., junior-heavy, mixed, senior-heavy"
                    />

                    {/* Languages */}
                    <Paper variant="outlined" sx={{ p: 2 }}>
                        <Stack spacing={1.5}>
                            <Typography variant="subtitle1" sx={{ fontWeight: 800 }}>
                                Team programming languages
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Select one or more.
                            </Typography>

                            <Stack direction="row" spacing={1} sx={{ flexWrap: "wrap", gap: 1 }}>
                                {(
                                    [
                                        [ProgrammingLanguages.JAVASCRIPT, "JavaScript"],
                                        [ProgrammingLanguages.PYTHON, "Python"],
                                        [ProgrammingLanguages.JAVA, "Java"],
                                        [ProgrammingLanguages.CSHARP, "C#"],
                                    ] as const
                                ).map(([value, label]) => {
                                    const selected = form.teamProgrammingLanguages.includes(value);
                                    return (
                                        <Chip
                                            key={value}
                                            label={label}
                                            clickable
                                            color={selected ? "primary" : "default"}
                                            variant={selected ? "filled" : "outlined"}
                                            onClick={() => toggleLanguage(value)}
                                            sx={{ fontWeight: 700 }}
                                        />
                                    );
                                })}
                            </Stack>

                            {selectedLanguageLabels.length > 0 && (
                                <Typography variant="body2" color="text.secondary">
                                    Selected: {selectedLanguageLabels.join(", ")}
                                </Typography>
                            )}
                        </Stack>
                    </Paper>

                    {/* Priority ranking */}
                    <Paper variant="outlined" sx={{ p: 2 }}>
                        <Typography variant="subtitle1" sx={{ fontWeight: 800, mb: 0.5 }}>
                            Priority ranking
                        </Typography>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                            Rank aspects by importance (top = most important).
                        </Typography>

                        <List dense>
                            {form.priorityAspects.map((aspect, idx) => (
                                <ListItem
                                    key={aspect}
                                    divider={idx !== form.priorityAspects.length - 1}
                                    secondaryAction={
                                        <Stack direction="row" spacing={0.5}>
                                            <Tooltip title="Move up">
                                                <span>
                                                    <IconButton
                                                        edge="end"
                                                        size="small"
                                                        onClick={() => moveAspect(idx, -1)}
                                                        disabled={idx === 0}
                                                    >
                                                        <ArrowUpwardIcon fontSize="small" />
                                                    </IconButton>
                                                </span>
                                            </Tooltip>
                                            <Tooltip title="Move down">
                                                <span>
                                                    <IconButton
                                                        edge="end"
                                                        size="small"
                                                        onClick={() => moveAspect(idx, 1)}
                                                        disabled={idx === form.priorityAspects.length - 1}
                                                    >
                                                        <ArrowDownwardIcon fontSize="small" />
                                                    </IconButton>
                                                </span>
                                            </Tooltip>
                                        </Stack>
                                    }
                                >
                                    <ListItemText
                                        primary={
                                            <Typography sx={{ fontWeight: 700 }}>
                                                {idx + 1}. {PRIORITY_ASPECT_LABELS[aspect]}
                                            </Typography>
                                        }
                                    />
                                </ListItem>
                            ))}
                        </List>
                    </Paper>

                    {/* TopRankN */}
                    <TextField
                        label="Number of recommendations to display"
                        type="number"
                        value={form.topRankN ?? ""}
                        inputProps={{ min: 1 }}
                        onChange={(e) => {
                            const val = Number(e.target.value);
                            if (val >= 1) {
                                setForm({
                                    ...form,
                                    topRankN: e.target.value === "" ? 4 : Number(e.target.value),
                                });
                            }
                        }}
                        fullWidth
                    />

                    <Divider />

                    {/* Submit */}
                    <Stack direction={{ xs: "column", sm: "row" }} spacing={2} alignItems="center">
                        <Button
                            type="submit"
                            variant="contained"
                            size="large"
                            disabled={loading}
                            sx={{ minWidth: 220 }}
                        >
                            {loading ? (
                                <Stack direction="row" spacing={1} alignItems="center">
                                    <CircularProgress size={18} />
                                    <span>Sending...</span>
                                </Stack>
                            ) : (
                                "Submit and save as draft"
                            )}
                        </Button>

                        <Typography variant="body2" color="text.secondary">
                            Your inputs are saved as a draft and used to generate recommendations.
                        </Typography>
                    </Stack>

                    {error && <Alert severity="error">Error: {error}</Alert>}
                </Stack>
            </form>
        </Box>
    );
}

export default QuestionnaireForm;