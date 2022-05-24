package com.github.tac.jagent.demo;

import javassist.*;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

/**
 * Agent 类，通过 agent.jar 中的 MANIFEST.MD 的 Premain-Class 属性指定
 *
 * @author taccisum - liaojinfeng6938@dingtalk.com
 * @since 2022/5/24
 */
public class MyAgent {
    /**
     * 该方法会在 main 方法之前被 jvm 调用
     * <br/>
     * 此方法必须遵循以下形式的约定：
     * public static void premain() {}
     */
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("------ MyAgent#premain() args: " + agentArgs + ". inst:" + inst.toString());
        System.out.println("------ MyAgent's classloader: " + MyAgent.class.getClassLoader());
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                String mainClass = "com.github.tac.jagent.demo.Main".replace(".", "/");
                if (loader.equals(MyAgent.class.getClassLoader())) {
                    ClassPool pool = getClassPool(loader);

                    if (className.equals(mainClass)) {
                        try {
                            System.out.println("------ Loading class: " + mainClass);
                            // CtClass is mean compiled-time class
                            CtClass c = pool.get(className.replace("/", "."));
                            // modify body of method #sayhello()
                            CtMethod m = c.getDeclaredMethod("sayhello");
                            System.out.println("------ Modify body of #sayhello");
                            m.setBody("System.out.println(\"hello javaagent. this content has modified by javassist!\");");
                            // return modified bytecode
                            return c.toBytecode();
                        } catch (NotFoundException | IOException | CannotCompileException e) {
                            // error happened. return origin bytecode
                            e.printStackTrace();
                            return classfileBuffer;
                        }
                    }
                }
                return classfileBuffer;
            }
        });
    }

    static Map<ClassLoader, ClassPool> polls = new HashMap<>();

    static ClassPool getClassPool(ClassLoader loader) {
        ClassPool pool = polls.get(loader);
        if (pool == null) {
            // instantiate javassit ClassPool for the class loader
            pool = new ClassPool(false);
            pool.insertClassPath(new LoaderClassPath(loader));
            polls.put(loader, pool);
            // notify hooks
            notifyPoolCreated(pool);
        }
        return pool;
    }

    static void notifyPoolCreated(ClassPool pool) {
        if (pool.getClassLoader().equals(MyAgent.class.getClassLoader())) {
            String className = "com.github.tac.jagent.demo.SayGoodBye";
            System.out.println("------ Create Class " + className);
            CtClass ctClass = pool.makeClass(className);
            try {
                CtMethod dosay = CtNewMethod.make("public void dosay() { System.out.println(\"byebye!\"); }", ctClass);
                ctClass.addMethod(dosay);
                ctClass.toClass();
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        }
    }
}
