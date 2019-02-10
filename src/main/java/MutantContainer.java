
class MutantContainer {
	private String mutantClass;
	private String methodName;
	private String mutantType;

	public String getMutatedClass() {
		return mutantClass;
	}

	public void setMutatedClass(String mutatedClass) {
		this.mutantClass = mutatedClass;
	}

	public String getMutationMethod() {
		return methodName;
	}

	public void setMutationMethod(String methodName) {
		this.methodName = methodName;
	}

	public String getMutantType() {
		return mutantType;
	}

	public void setMutantType(String mutantType) {
		this.mutantType = mutantType;
	}

}
