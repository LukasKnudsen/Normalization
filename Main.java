import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Read attributes
        System.out.println("Enter attributes separated by commas (e.g. A,B,C,D):");
        String attrLine = scanner.nextLine();
        Set<Attribute> attributes = new HashSet<>();
        for (String attr : attrLine.split(",")) {
            attributes.add(new Attribute(attr.trim()));
        }

        // Step 2: Read functional dependencies
        System.out.println("Enter functional dependencies (e.g. A->B;C,D->E):");
        String fdLine = scanner.nextLine();
        Set<FuncDep> fds = new HashSet<>();
        for (String fdString : fdLine.split(";")) {
            String[] parts = fdString.split("->");
            if (parts.length != 2) continue;

            Set<Attribute> lhs = new HashSet<>();
            for (String s : parts[0].split(",")) {
                lhs.add(new Attribute(s.trim()));
            }

            Set<Attribute> rhs = new HashSet<>();
            for (String s : parts[1].split(",")) {
                rhs.add(new Attribute(s.trim()));
            }

            fds.add(new FuncDep(lhs, rhs));
        }

        // Step 3: Create relation and perform decompositions
        Relation relation = new Relation(attributes, fds);

        System.out.println("\n--- Candidate Keys ---");
        Set<Set<Attribute>> candidateKeys = Algos.keys(relation.getAttrs(), relation.getFds());
        int index = 1;
        for (Set<Attribute> key : candidateKeys) {
            System.out.print("Key " + index++ + ": ");
            System.out.println(printAttributes(key));
        }


        System.out.println("\n\n--- Violating FDs ---");

        Set<FuncDep> violating3NF = Algos.check3NF(relation.getAttrs(), relation.getFds());
        System.out.println("\nFDs violating 3NF:");
        if (violating3NF.isEmpty()) {
            System.out.println("✔ None");
        } else {
            for (FuncDep fd : violating3NF) {
                System.out.println(" - " + fd);
            }
        }

        Set<FuncDep> violatingBCNF = Algos.checkBCNF(relation.getAttrs(), relation.getFds());
        System.out.println("\nFDs violating BCNF:");
        if (violatingBCNF.isEmpty()) {
            System.out.println("✔ None");
        } else {
            for (FuncDep fd : violatingBCNF) {
                System.out.println(" - " + fd);
            }
        }



        System.out.println("\n\n--- 3NF Decomposition ---");
        List<Relation> threeNF = Algos.threeNFDecompose(relation);
        for (Relation r : threeNF) {
            System.out.println(r);
        }
        checkDecompositionLoss("3NF", relation.getAttrs(), relation.getFds(), threeNF);


        System.out.println("\n\n--- BCNF Decomposition ---");
        List<Relation> bcnf = Algos.BCNFDecompose(relation);
        for (Relation r : bcnf) {
            System.out.println(r);
        }
        checkDecompositionLoss("BCNF", relation.getAttrs(), relation.getFds(), bcnf);




    }

    private static void checkDecompositionLoss(String name, Set<Attribute> originalAttrs, Set<FuncDep> originalFds, List<Relation> decomposed) {
        Set<Set<Attribute>> subSchemas = new HashSet<>();
        for (Relation r : decomposed) {
            subSchemas.add(r.getAttrs());
        }

        Set<FuncDep> lost = Algos.checkLossyDecomposition(originalAttrs, originalFds, subSchemas);
        if (lost.isEmpty()) {
            System.out.println("✔ " + name + " decomposition is lossless.");
        } else {
            System.out.println("❌ " + name + " decomposition loses the following FDs:");
            for (FuncDep fd : lost) {
                System.out.println("   " + fd);
            }
        }
    }

    private static String printAttributes(Set<Attribute> attrs) {
        List<String> names = new ArrayList<>();
        for (Attribute a : attrs) {
            names.add(a.toString());
        }
        Collections.sort(names);
        return String.join(",", names);
    }


}
