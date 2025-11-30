import { FormEvent, useState } from "react";

const ArchitectureScope = {
    BACKEND_ONLY: 0 as const,
    FULL_STACK: 1 as const,
    MOBILE: 2 as const,
};

const ProgrammingLanguages = {
    JAVASCRIPT: 0 as const,
    PYTHON: 1 as const,
    JAVA: 2 as const,
    CSHARP: 3 as const,
};

const DeploymentPreferences = {
    SELF_HOSTED: 0 as const,
    PAAS: 1 as const,
    CLOUD_NATIVE: 2 as const,
    SERVERLESS: 3 as const,
    KUBERNETES: 4 as const,
    ON_PREM: 5 as const,
    HYBRID: 6 as const,
};

const PriorityAspects = {
    PERFORMANCE: 0 as const,
    SCALABILITY: 1 as const,
    MAINTAINABILITY: 2 as const,
    SECURITY: 3 as const,
    COST_EFFECTIVENESS: 4 as const,
    COMMUNITY_SUPPORT: 5 as const,
    ECOSYSTEM_MATURITY: 6 as const,
    VENDOR_LOCKIN_AVOIDANCE: 7 as const,
}



type ArchitectureScope = typeof ArchitectureScope[keyof typeof ArchitectureScope];
type DeploymentPreferences = typeof DeploymentPreferences[keyof typeof DeploymentPreferences];
type ProgrammingLanguages = typeof ProgrammingLanguages[keyof typeof ProgrammingLanguages];
type PriorityAspects = typeof PriorityAspects[keyof typeof PriorityAspects];

type QuestionnaireRequest = {
    architectureScope: ArchitectureScope | null;
    isOpenSource: boolean;
    deploymentPreference: DeploymentPreferences | null;
    expectedUsers: number | null;
    teamSize: number;
    experienceLevel: string;
    teamProgrammingLanguages: ProgrammingLanguages[];
    priorityAspects: PriorityAspects[];
};

type QuestionnaireResponse = {
    //TODO: Define according to backend response

};

function QuestionnaireForm() {
    const [form, setForm] = useState<QuestionnaireRequest>({
        architectureScope: null,
        isOpenSource: false,
        deploymentPreference: null,
        expectedUsers: null,
        teamSize: 0,
        experienceLevel: "",
        priorityAspects: [],
        teamProgrammingLanguages: [],
    });

    const [loading, setLoading] = useState(false);
    const [result, setResult] = useState<QuestionnaireResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    const toggleLanguage = (lang: ProgrammingLanguages) => {
        setForm(prev => {
            const selected = prev.teamProgrammingLanguages;
            if (selected.includes(lang)) {
                // remove if already selected
                return {
                    ...prev,
                    teamProgrammingLanguages: selected.filter(l => l !== lang),
                };
            } else {
                // add if not yet selected
                return {
                    ...prev,
                    teamProgrammingLanguages: [...selected, lang],
                };
            }
        });
    };
    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setResult(null);

        try {
            const response = await fetch("/api/questionnaire", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    name: form.name,
                    age: form.age === "" ? null : Number(form.age),
                    likesIdea: form.likesIdea,
                    feedback: form.feedback,
                }),
            });

            if (!response.ok) {
                throw new Error(`Backend returned status ${response.status}`);
            }

            const data: QuestionnaireResponse = await response.json();
            setResult(data);
        } catch (err: any) {
            console.error(err);
            setError(err.message ?? "Unknown error");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ maxWidth: 500, margin: "2rem auto", fontFamily: "sans-serif" }}>
            <h1>MVP Questionnaire</h1>

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
                                    architectureScope: e.target.value === "" ? null : Number(e.target.value),
                                })
                            }
                            style={{ marginLeft: "0.5rem", width: "100%" }}
                        >
                            <option value="">Select an option</option>
                            <option value={ArchitectureScope.BACKEND_ONLY}>Backend Only</option>
                            <option value={ArchitectureScope.FULL_STACK}>Full Stack</option>
                            <option value={ArchitectureScope.MOBILE}>Mobile</option>
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
                                    deploymentPreference: e.target.value === "" ? null : Number(e.target.value),
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
                {/*Number of expected users*/}
                <div style={{ marginBottom: "1rem" }}>
                    <label>
                        Expected Number of Users:
                        <input
                            type="number"
                            value={form.expectedUsers ?? ""}
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
                            value={form.teamSize ?? ""}
                            onChange={(e) =>
                                setForm({
                                    ...form,
                                    teamSize: e.target.value === "" ? null : Number(e.target.value),
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
                <button type="submit" disabled={loading}>
                    {loading ? "Sending..." : "Submit"}
                </button>
            </form>

            {error && (
                <p style={{ color: "red", marginTop: "1rem" }}>
                    Error: {error}
                </p>
            )}

            {result && (
                <div
                    style={{
                        marginTop: "1.5rem",
                        padding: "1rem",
                        border: "1px solid #ccc",
                        borderRadius: "4px",
                    }}
                >
                    <h2>Backend Response</h2>
                    <p><strong>Summary:</strong> {result.summary}</p>
                    <p><strong>Score:</strong> {result.score}</p>
                </div>
            )}
        </div>
    );
}

export default QuestionnaireForm;
