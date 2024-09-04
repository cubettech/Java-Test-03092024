import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class Examinee {
    private long id;
    private Map<String, Float> subjectMarks;

    public Examinee(long id, Map<String, Float> subjectMarks) {
        this.id = id;
        this.subjectMarks = new HashMap<>(subjectMarks);
    }

    public long getId() {
        return id;
    }

    public Map<String, Float> getSubjectMarks() {
        return new HashMap<>(subjectMarks);
    }

    @Override
    public String toString() {
        return "Examinee{id=" + id + ", subjectMarks=" + subjectMarks + "}";
    }
}

class ExamineeRanking {
    private static List<Examinee> examinees;

    public static void main(String[] args) {
        
        init();


        List<Examinee> rankedExaminees = getRankedExaminees(examinees);


        rankedExaminees.forEach(System.out::println);
    }


    private static void init() {
        examinees = Arrays.asList(
            new Examinee(1, Map.of("Math", 85f, "Science", 78f, "English", 90f)),
            new Examinee(2, Map.of("Math", 30f, "Science", 45f, "English", 60f)), // Fails in Math
            new Examinee(3, Map.of("Math", 95f, "Science", 88f, "English", 92f)),
            new Examinee(4, Map.of("Math", 70f, "Science", 65f, "English", 80f)),
            new Examinee(5, Map.of("Math", 40f, "Science", 25f, "English", 70f))  // Fails in Science
        );
    }


    public static List<Examinee> getRankedExaminees(List<Examinee> examinees) {
        return examinees.stream()
            .map(ExamineeRanking::createDuplicates)
            .filter(examinee -> examinee.getSubjectMarks().values().stream().allMatch(score -> score >= 35))
            .sorted(Comparator.comparingDouble(ExamineeRanking::getAggregate).reversed())
            .collect(Collectors.toList());
    }


    private static Examinee createDuplicates(Examinee examinee) {
        return new Examinee(examinee.getId(), examinee.getSubjectMarks());
    }


    private static double getAggregate(Examinee examinee) {
        return examinee.getSubjectMarks().values().stream()
            .mapToDouble(Float::doubleValue)
            .sum();
    }
}