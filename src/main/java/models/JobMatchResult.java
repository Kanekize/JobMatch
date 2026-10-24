package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Result of comparing one job against a seeker's assessment. */
public final class JobMatchResult {
    private final int jobId;
    private final int matchPercent;
    private final String reason;
    private final List<String> matchedSkills;
    private final List<String> skillGaps;
    private final boolean aiGenerated;

    public JobMatchResult(int jobId, int matchPercent, String reason,
                          List<String> matchedSkills, List<String> skillGaps,
                          boolean aiGenerated) {
        this.jobId = jobId;
        this.matchPercent = Math.max(0, Math.min(100, matchPercent));
        this.reason = reason == null ? "" : reason.trim();
        this.matchedSkills = immutableCopy(matchedSkills);
        this.skillGaps = immutableCopy(skillGaps);
        this.aiGenerated = aiGenerated;
    }

    private static List<String> immutableCopy(List<String> values) {
        if (values == null || values.isEmpty()) return Collections.emptyList();
        List<String> copy = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.isBlank()) copy.add(value.trim());
        }
        return Collections.unmodifiableList(copy);
    }

    public int getJobId() { return jobId; }
    public int getMatchPercent() { return matchPercent; }
    public String getReason() { return reason; }
    public List<String> getMatchedSkills() { return matchedSkills; }
    public List<String> getSkillGaps() { return skillGaps; }
    public boolean isAiGenerated() { return aiGenerated; }
}
