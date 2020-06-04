package jcf.extproc.process;



public interface ExternalProcessVisitor {

	void visit(CommandLineProcess commandLineProcess);

	void visit(AntProcess antProcess);

}
