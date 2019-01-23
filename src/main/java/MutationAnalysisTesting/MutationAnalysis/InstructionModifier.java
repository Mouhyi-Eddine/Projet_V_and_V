package MutationAnalysisTesting.MutationAnalysis;

import org.objectweb.asm.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import static org.objectweb.asm.Opcodes.ASM5;

public class InstructionModifier extends ClassVisitor {


    public InstructionModifier(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);;

        if(name.equals("twice")) { // Too specific uh? It is better to have a parameter
            return new StartByAddModifier(api, visitor);
        }
        return visitor;
    }

    static class StartByAddModifier extends MethodVisitor {

        public StartByAddModifier(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        @Override
        public void visitInsn(int opcode) {
            if (opcode == Opcodes.DMUL) {
                opcode = Opcodes.DADD;
            }else if (opcode == Opcodes.LAND) {
            	opcode = Opcodes.LOR;
            }
            super.visitInsn(opcode);
        }
    }

    public static void main(String... args) throws Exception {

        final String target = "/home/mj/vv-workspace/code-manipulation/manipulation-target/target/classes/m2/vv/tutorial/Functions.class";

        FileInputStream file = new FileInputStream(target);
        ClassReader reader = new ClassReader(file);

        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);

        reader.accept(new InstructionModifier(ASM5, writer), ClassReader.EXPAND_FRAMES);

        FileOutputStream output = new FileOutputStream(target);
        output.write(writer.toByteArray());

    }
}

