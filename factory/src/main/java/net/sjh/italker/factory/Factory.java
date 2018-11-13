package net.sjh.italker.factory;


import net.sjh.italker.common.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Factory {

    private static final Factory instance;
    private final Executor executor;


    static {
        instance = new Factory();
    }
    private Factory(){
        //新建一个4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
    }

    //返回全局的application
    public static Application app(){
        return Application.getInstance();
    }


    /**
     * 异步运行的方法
     * */
    public static void runOnAsync(Runnable runnable){
        //拿到单列，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

}
