



/**
 * Mutation class describes mutation information use by the mutator
 */
public class Mutation {

    private String targetOperation;
    private int targetOperationCode;

    private String mutationOperation;
    private int mutationOperationCode;
    private String mutationType;

    public Mutation(String targetOperation, int targetOperationCode, String mutationOperation, int mutationOperationCode, String typeMutation) {
        this.targetOperation = targetOperation;
        this.targetOperationCode = targetOperationCode;
        
        this.mutationOperation = mutationOperation;
        this.mutationOperationCode = mutationOperationCode;
        this.setMutationType(typeMutation);
        
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

	public String getMutationType() {
		return mutationType;
	}

	public void setMutationType(String mutationType) {
		this.mutationType = mutationType;
	}
}