import { type FormEvent, useState, useEffect, useMemo } from "react";
import { useNavigate, useParams } from "react-router-dom";



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
    architectureScope: string | null;
    deploymentPreference: DeploymentPreferences | null;
    //only when deployment == CLOUD TODO
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
    //TODO: Define according to backend response

};

function QuestionnaireForm() {
    const [form, setForm] = useState<QuestionnaireRequest>({
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
    const isEditMode = Boolean(draftId);

    // base URL for printing / sharing
    const draftLink = useMemo(() => {
        if (!draftId) return null;
        return `${window.location.origin}/draft/${draftId}`;
    }, [draftId]);
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
                };

                setForm(loaded);
            } catch (e: any) {
                setError(e?.message ?? "Could not load draft");
            } finally {
                setLoading(false);
            }
        })();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [draftId]);

    const saveDraft = async (): Promise<string> => {
        // Build backend DTO payload (match backend field names)
        const payload = {
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

            // if it was created, move user to /draft/:id so they have the shareable link
            if (!draftId) {
                navigate(`/draft/${savedId}`, { replace: true });
            }
            var body = JSON.stringify({
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
            console.log("Draft-Id:", savedId);
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
            navigate("/results", { state: { result: data, draftLink: draftLink, draftId: savedId } });
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Unknown error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 500, margin: "2rem auto", fontFamily: "sans-serif" }}>
            <h1>Questionnaire</h1>
            {draftLink && (
                <div style={{ marginBottom: "1rem", padding: "0.75rem", border: "1px solid #ccc", borderRadius: 6 }}>
                    <div style={{ fontSize: "0.9rem", marginBottom: "0.25rem" }}>
                        Editing draft:
                    </div>
                    <a href={draftLink}>{draftLink}</a>
                    <button
                        type="button"
                        style={{ marginLeft: "0.5rem" }}
                        onClick={() => navigator.clipboard.writeText(draftLink)}
                    >
                        Copy
                    </button>
                </div>
            )}

            <form onSubmit={handleSubmit}>
                {/* Scope */}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Architecture Scope:
                        <select
                            value={form.architectureScope ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    architectureScope: e.target.value === "" ? null : e.target.value,
                                })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        >
                            <option value="">Select an option</option>
                            <option value={"BACKEND_ONLY"}>Backend Only</option>
                            <option value={"FULL_STACK"}>Full Stack</option>
                            <option value={"MOBILE"}>Mobile</option>
                        </select>
                    </label>
                </div>
                {/*isOpenSource*/}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Only propose OpenSource frameworks?
                        <input
                            type="checkbox"
                            checked={form.isOpenSource}
                            onChange={(e) =>
                                setForm({ ...form, isOpenSource: e.target.checked })
                            }
                            style={{ marginLeft: "0.5rem" }}
                        />
                    </label>
                </div>

                {/* Deployment Preference */}
                <div style={{ marginBottom: "1rem" }}>
                    <label> What is your deployment preference?
                        <select
                            value={form.deploymentPreference ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    deploymentPreference: e.target.value === "" ? null : (e.target.value as DeploymentPreferences),
                                })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        >
                            <option value="">Select an option</option>
                            <option value={DeploymentPreferences.SELF_HOSTED}>Self-Hosted</option>
                            <option value={DeploymentPreferences.PAAS}>Platform as a Service (PaaS)</option>
                            <option value={DeploymentPreferences.CLOUD_NATIVE}>Cloud-Native</option>
                            <option value={DeploymentPreferences.SERVERLESS}>Serverless</option>
                            <option value={DeploymentPreferences.KUBERNETES}>Kubernetes</option>
                            <option value={DeploymentPreferences.ON_PREM}>On-Premises</option>
                            <option value={DeploymentPreferences.HYBRID}>Hybrid</option>
                        </select>
                    </label>
                </div>
                {/* BudgetTier */}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Which budgetTier are you planning to use?:
                        <select
                            value={form.budgetTier ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    budgetTier: e.target.value === "" ? null : (e.target.value as BudgetTier),
                                })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        >
                            <option value="">Select an option</option>
                            <option value={BudgetTier.LOW}>Low</option>
                            <option value={BudgetTier.MEDIUM}>Medium</option>
                            <option value={BudgetTier.HIGH}>High</option>
                        </select>
                    </label>
                </div>
                {/*isServerlessFriendly*/}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Should the selected technologies be serverless friendly?
                        <input
                            type="checkbox"
                            checked={form.isServerlessFriendly}
                            onChange={(e) =>
                                setForm({ ...form, isServerlessFriendly: e.target.checked })
                            }
                            style={{ marginLeft: "0.5rem" }}
                        />
                    </label>
                </div>
                {/*Number of expected users*/}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Expected Number of Users:
                        <input
                            type="number"
                            value={form.expectedUsers ?? ""}
                            min={1}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    expectedUsers: e.target.value === "" ? null : Number(e.target.value),
                                })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        />
                    </label>
                </div>
                {/* Team Size */}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Team Size:
                        <input
                            type="number"
                            min={1}
                            value={form.teamSize ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    teamSize: e.target.value === "" ? 1 : Number(e.target.value),
                                })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        />
                    </label>
                </div>
                {/* Experience Level */}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Experience Level:
                        <input
                            type="text"
                            value={form.experienceLevel}
                            onChange={(e) =>
                                setForm({ ...form, experienceLevel: e.target.value })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        />
                    </label>
                </div>

                {/* Language familiarity */}
                <div style={{ marginBottom: "1rem" }}>
                    <p>Select one or more languages:</p>

                    <label>
                        <input
                            type="checkbox"
                            checked={form.teamProgrammingLanguages.includes(ProgrammingLanguages.JAVASCRIPT)}
                            onChange={() => toggleLanguage(ProgrammingLanguages.JAVASCRIPT)}
                        />
                        JavaScript
                    </label>
                    <br />

                    <label>
                        <input
                            type="checkbox"
                            checked={form.teamProgrammingLanguages.includes(ProgrammingLanguages.PYTHON)}
                            onChange={() => toggleLanguage(ProgrammingLanguages.PYTHON)}
                        />
                        Python
                    </label>
                    <br />

                    <label>
                        <input
                            type="checkbox"
                            checked={form.teamProgrammingLanguages.includes(ProgrammingLanguages.JAVA)}
                            onChange={() => toggleLanguage(ProgrammingLanguages.JAVA)}
                        />
                        Java
                    </label>
                    <br />

                    <label>
                        <input
                            type="checkbox"
                            checked={form.teamProgrammingLanguages.includes(ProgrammingLanguages.CSHARP)}
                            onChange={() => toggleLanguage(ProgrammingLanguages.CSHARP)}
                        />
                        C#
                    </label>
                </div>
                {/* Priority Aspects Ranking */}
                <div style={{ marginBottom: "1rem" }}>
                    <p>Rank the aspects by priority (top = most important):</p>

                    <ol>
                        {form.priorityAspects.map((aspect, idx) => (
                            <li key={aspect} style={{ marginBottom: "0.5rem" }}>
                                {PRIORITY_ASPECT_LABELS[aspect]}
                                <span style={{ marginLeft: "0.5rem" }}>
                                    <button
                                        type="button"
                                        onClick={() => moveAspect(idx, -1)}
                                        disabled={idx === 0}
                                        style={{ marginRight: "0.25rem" }}
                                    >
                                        ↑
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => moveAspect(idx, 1)}
                                        disabled={idx === form.priorityAspects.length - 1}
                                    >
                                        ↓
                                    </button>
                                </span>
                            </li>
                        ))}
                    </ol>
                </div>
                {/*TopRankN*/}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        What number of top rank recommendation should be displayed?:
                        <input
                            type="number"
                            value={form.topRankN ?? ""}
                            min={1}
                            onChange={(e) => {
                                const val = Number(e.target.value);
                                if (val >= 1) {
                                    setForm({
                                        ...form,
                                        topRankN: e.target.value === "" ? 4 : Number(e.target.value),
                                    });
                                }
                            }}
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        />
                    </label>
                </div>
                {/* Submit Button */}
                <button type="submit" disabled={loading}>
                    {loading ? "Sending..." : "Submit and save as draft"}
                </button>
            </form>

            {error && (
                <p style={{ color: "red", marginTop: "1rem" }}>
                    Error: {error}
                </p>
            )}

        </div>
    );
}

export default QuestionnaireForm;