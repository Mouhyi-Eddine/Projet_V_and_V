package MutationAnalysisTesting.MutationAnalysis;

import java.io.FileInputStream;
import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.ASM5;

import javassist.ClassPool;
import javassist.CodeConverter;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.Opcode;

public class App extends ClassVisitor{
    public App(int api) {
        super(api);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println(String.format("%s %s", name, desc));
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    public static void main(String... args) throws IOException {

        // Intended to be executed from the root of the project

        FileInputStream file = new FileInputStream("/home/mj/vv-workspace/code-manipulation/manipulation-target/target/classes/m2/vv/tutorial/Functions.class");
        ClassReader reader = new ClassReader(file);
        reader.accept(new App(ASM5), 0);

    }
}
