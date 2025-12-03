package com.company.test;

import com.company.jsonlib.JsonTool;

import java.util.ArrayList;
import java.util.List;

public class MainClass {

    public static void main(String[] args) {
        JsonTool jsonTool = new JsonTool();

        System.out.println("=".repeat(80));
        System.out.println("TESTS DU SÉRIALISEUR/DÉSÉRIALISEUR JSON");
        System.out.println("=".repeat(80));

        test1_CoursDTOSimple(jsonTool);
        test2_FieldNameAnnotation(jsonTool);
        test3_IgnoreAnnotation(jsonTool);
        test4_StudentWithCollection(jsonTool);
        test5_ProgramWithArray(jsonTool);
        test6_ComplexNesting(jsonTool);

        System.out.println("\n" + "=".repeat(80));
        System.out.println("TOUS LES TESTS SONT TERMINÉS");
        System.out.println("=".repeat(80));
    }

    private static void test1_CoursDTOSimple(JsonTool jsonTool) {
        System.out.println("\n>>> TEST 1: CoursDTO Simple");
        System.out.println("-".repeat(80));

        CoursDTO cours = new CoursDTO("INF1035", "Programmation Orientée Objet",
                "Concepts avancés en POO", 45);

        String json = jsonTool.toJson(cours);
        System.out.println("JSON sérialisé:");
        System.out.println(json);

        CoursDTO coursDeserialized = jsonTool.toDTO(json, CoursDTO.class);
        System.out.println("\nObjet désérialisé:");
        System.out.println(coursDeserialized);

        boolean success = cours.getCode().equals(coursDeserialized.getCode()) &&
                cours.getName().equals(coursDeserialized.getName());
        System.out.println("\n✓ Test 1: " + (success ? "RÉUSSI" : "ÉCHOUÉ"));
    }

    private static void test2_FieldNameAnnotation(JsonTool jsonTool) {
        System.out.println("\n>>> TEST 2: Annotation @FieldName");
        System.out.println("-".repeat(80));

        CoursDTO cours = new CoursDTO("MAT1000", "Mathématiques", "Calcul différentiel", 50);

        String json = jsonTool.toJson(cours);
        System.out.println("JSON sérialisé:");
        System.out.println(json);

        boolean hasOverride = json.contains("\"titre\"");
        System.out.println("\n✓ Test 2 (@FieldName): " + (hasOverride ? "RÉUSSI" : "ÉCHOUÉ"));
        System.out.println("  - Le champ 'name' est bien renommé en 'titre'");
    }

    private static void test3_IgnoreAnnotation(JsonTool jsonTool) {
        System.out.println("\n>>> TEST 3: Annotation @Ignore");
        System.out.println("-".repeat(80));

        StudentDTO student = new StudentDTO(1, "Jean", "Dupont", 21, "M");
        student.setInternalNote("Note interne confidentielle");

        String json = jsonTool.toJson(student);
        System.out.println("JSON sérialisé:");
        System.out.println(json);

        boolean isIgnored = !json.contains("internalNote");
        System.out.println("\n✓ Test 3 (@Ignore): " + (isIgnored ? "RÉUSSI" : "ÉCHOUÉ"));
        System.out.println("  - Le champ 'internalNote' est bien ignoré");
    }

    private static void test4_StudentWithCollection(JsonTool jsonTool) {
        System.out.println("\n>>> TEST 4: StudentDTO avec Collection");
        System.out.println("-".repeat(80));

        StudentDTO student = new StudentDTO(101, "Marie", "Tremblay", 20, "F");

        List<CoursDTO> cours = new ArrayList<>();
        cours.add(new CoursDTO("INF1035", "POO", "Programmation orientée objet", 45));
        cours.add(new CoursDTO("MAT1000", "Calcul", "Calcul différentiel", 50));
        cours.add(new CoursDTO("INF1018", "Structures de données", "Listes, arbres, graphes", 40));

        student.setInscriptions(cours);

        String json = jsonTool.toJson(student);
        System.out.println("JSON sérialisé:");
        System.out.println(json);

        StudentDTO studentDeserialized = jsonTool.toDTO(json, StudentDTO.class);
        System.out.println("\nObjet désérialisé:");
        System.out.println(studentDeserialized);

        boolean success = studentDeserialized.getInscriptions() != null &&
                studentDeserialized.getInscriptions().size() == 3;
        System.out.println("\n✓ Test 4: " + (success ? "RÉUSSI" : "ÉCHOUÉ"));
    }

    private static void test5_ProgramWithArray(JsonTool jsonTool) {
        System.out.println("\n>>> TEST 5: ProgramDTO avec Tableau");
        System.out.println("-".repeat(80));

        ProgramDTO program = new ProgramDTO("Baccalauréat en informatique", "7625", 1, true);

        CoursDTO[] composition = new CoursDTO[3];
        composition[0] = new CoursDTO("INF1035", "POO", "Programmation orientée objet", 45);
        composition[1] = new CoursDTO("INF1018", "Structures", "Structures de données", 40);
        composition[2] = new CoursDTO("MAT1000", "Calcul", "Calcul différentiel", 50);

        program.setComposition(composition);

        String json = jsonTool.toJson(program);
        System.out.println("JSON sérialisé:");
        System.out.println(json);

        boolean hasFieldName = json.contains("\"nomProgramme\"");
        System.out.println("\n✓ Test 5: " + (hasFieldName ? "RÉUSSI" : "ÉCHOUÉ"));
    }

    private static void test6_ComplexNesting(JsonTool jsonTool) {
        System.out.println("\n>>> TEST 6: Objets Imbriqués Complexes");
        System.out.println("-".repeat(80));

        StudentDTO student = new StudentDTO(202, "Pierre", "Gagnon", 22, "M");

        List<CoursDTO> inscriptions = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            inscriptions.add(new CoursDTO(
                    "INF" + (1000 + i),
                    "Cours " + i,
                    "Description du cours " + i,
                    30 + i * 5
            ));
        }
        student.setInscriptions(inscriptions);

        String json = jsonTool.toJson(student);
        System.out.println("JSON sérialisé (extrait):");
        System.out.println(json.substring(0, Math.min(300, json.length())) + "...");

        StudentDTO studentDeserialized = jsonTool.toDTO(json, StudentDTO.class);

        boolean success = studentDeserialized.getId() == 202 &&
                studentDeserialized.getInscriptions().size() == 5 &&
                studentDeserialized.getInscriptions().get(0).getCode().equals("INF1001");

        System.out.println("\nVérifications:");
        System.out.println("  - ID: " + studentDeserialized.getId() + " (attendu: 202)");
        System.out.println("  - Nombre de cours: " + studentDeserialized.getInscriptions().size() + " (attendu: 5)");
        System.out.println("  - Premier cours: " + studentDeserialized.getInscriptions().get(0).getCode());
        System.out.println("\n✓ Test 6: " + (success ? "RÉUSSI" : "ÉCHOUÉ"));
    }
}