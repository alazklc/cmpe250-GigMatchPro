import java.util.ArrayList;
import java.util.LinkedList;

public class ServiceHelper {

    private static final MyHashMap<String, int[]> serviceProfiles = new MyHashMap<>();
    private static final MyHashMap<String, Integer> serviceMaxScores = new MyHashMap<>();
    private static final String[] serviceNames = {
            "paint", "web_dev", "graphic_design", "data_entry", "tutoring",
            "cleaning", "writing", "photography", "plumbing", "electrical"
    };

    static {
        serviceProfiles.put("paint", new int[]{70, 60, 50, 85, 90});
        serviceProfiles.put("web_dev", new int[]{95, 75, 85, 80, 90});
        serviceProfiles.put("graphic_design", new int[]{75, 85, 95, 70, 85});
        serviceProfiles.put("data_entry", new int[]{50, 50, 30, 95, 95});
        serviceProfiles.put("tutoring", new int[]{80, 95, 70, 90, 75});
        serviceProfiles.put("cleaning", new int[]{40, 60, 40, 90, 85});
        serviceProfiles.put("writing", new int[]{70, 85, 90, 80, 95});
        serviceProfiles.put("photography", new int[]{85, 80, 90, 75, 90});
        serviceProfiles.put("plumbing", new int[]{85, 65, 60, 90, 85});
        serviceProfiles.put("electrical", new int[]{90, 65, 70, 95, 95});

        for (String service : serviceNames) {
            int[] profile = serviceProfiles.get(service);
            int sum = 0;
            for (int val : profile) {
                sum += val;
            }
            serviceMaxScores.put(service, 100 * sum);
        }
    }

    public static int[] getSkillProfile(String serviceName) {
        return serviceProfiles.get(serviceName);
    }

    public static int getTheoreticalMaxScore(String serviceName) {
        return serviceMaxScores.get(serviceName);
    }

    public static String[] getServiceNames() {
        return serviceNames;
    }

    public static int calculateSkillDotProduct(int[] freelancerSkills, int[] serviceProfile) {
        int dotProduct = 0;
        for (int i = 0; i < 5; i++) {
            dotProduct += freelancerSkills[i] * serviceProfile[i];
        }
        return dotProduct;
    }

    public static boolean isHigherPriority(Freelancer f1, Freelancer f2) {
        if (f1.isAvailable && !f2.isAvailable) {
            return true;
        }
        if (!f1.isAvailable && f2.isAvailable) {
            return false;
        }
        if (f1.compositeScore != f2.compositeScore) {
            return f1.compositeScore > f2.compositeScore;
        }
        return f1.id.compareTo(f2.id) < 0;
    }

    private static boolean isHigherPriority(int[] profile, int idx1, int idx2) {
        if (idx2 == -1) return true;
        if (idx1 == -1) return false;

        int val1 = profile[idx1];
        int val2 = profile[idx2];

        if (val1 != val2) {
            return val1 > val2;
        }
        return idx1 < idx2;
    }

    /*
     * Applies skill gains following a successful job (rating >= 4).
     * 1. Identifies the Primary skill (highest requirement) and increases it by +2.
     * 2. Identifies the next two highest skills (Secondary) and increases them by +1.
     * 3. Tie-breaking for skill selection follows the order: T, C, R, E, A.
     * 4. Skills are capped at 100.
     */
    public static void applySkillGains(int[] freelancerSkills, String serviceName) {
        int[] serviceProfile = getSkillProfile(serviceName);

        int primaryIdx = -1;
        int secondIdx = -1;
        int thirdIdx = -1;

        for (int i = 0; i < 5; i++) {
            if (isHigherPriority(serviceProfile, i, primaryIdx)) {
                primaryIdx = i;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (i == primaryIdx) continue;
            if (isHigherPriority(serviceProfile, i, secondIdx)) {
                secondIdx = i;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (i == primaryIdx || i == secondIdx) continue;
            if (isHigherPriority(serviceProfile, i, thirdIdx)) {
                thirdIdx = i;
            }
        }

        if (primaryIdx != -1) freelancerSkills[primaryIdx] += 2;
        if (secondIdx != -1) freelancerSkills[secondIdx] += 1;
        if (thirdIdx != -1) freelancerSkills[thirdIdx] += 1;

        for (int i = 0; i < 5; i++) {
            if (freelancerSkills[i] > 100) {
                freelancerSkills[i] = 100;
            }
        }
    }
}