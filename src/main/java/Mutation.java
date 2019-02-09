



/**
 * Mutation class describes mutation information use by the mutator
 */
public class Mutation {

    private String targetOperation;
    private int targetOperationCode;

    private String mutationOperation;
    private int mutationOperationCode;


    public Mutation(String targetOperation, int targetOperationCode, String mutationOperation, int mutationOperationCode) {
        this.targetOperation = targetOperation;
        this.targetOperationCode = targetOperationCode;

        this.mutationOperation = mutationOperation;
        this.mutationOperationCode = mutationOperationCode;

        
    }

    public String getTargetOperation() {
        return targetOperation;
    }

    public int getTargetOperationCode() {
        return targetOperationCode;
    }

    public int getMutationOperationCode() {
        return mutationOperationCode;
    }


    @Override
    public String toString() {
        return "Mutation{  "+targetOperation+"#"+targetOperationCode+" -> "+mutationOperation+"#"+mutationOperationCode+" }";
    }
}