package com.thowo.jmjavaframework;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Created by jimi on 8/16/2017.
 */

public class JMAsyncTask{
    private JMAsyncListener el=null;
    private String errorMessage="";
    private Object result=null;
    private Integer timeOut=1;
    private String id="";
    private Callable<?> task;
    private ExecutorService executor;
    private Future<?> future;

    public JMAsyncTask(JMAsyncListener el, Callable<?> task, String id){
        this.el=el;
        this.id=id;
        this.task=task;
        this.process();
    }

    public JMAsyncTask(JMAsyncListener el, Callable<?> task, Integer timeOut){
        this.el=el;
        this.id=id;
        this.timeOut=timeOut;
        this.task=task;
        this.process();
    }

    private void process(){
        if(this.el!=null){
            this.el.onJMStart(this.id);
            JMAsyncTask.this.executor=Executors.newFixedThreadPool(1);
            JMAsyncTask.this.future=executor.submit(JMAsyncTask.this.task);

            while (!this.future.isDone()){
                JMAsyncTask.this.el.onJMProcess(JMAsyncTask.this.id);

            }
            try {
                JMAsyncTask.this.result=future.get(this.timeOut, TimeUnit.MINUTES);
                JMAsyncTask.this.el.onJMComplete(JMAsyncTask.this.result, JMAsyncTask.this.id);
            } catch (InterruptedException ex) {
                Logger.getLogger(JMAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
                JMAsyncTask.this.errorMessage=ex.getMessage();
                JMAsyncTask.this.el.onJMError(JMAsyncTask.this.errorMessage, JMAsyncTask.this.id);
            } catch (ExecutionException ex) {
                Logger.getLogger(JMAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
                JMAsyncTask.this.errorMessage=ex.getMessage();
                JMAsyncTask.this.el.onJMError(JMAsyncTask.this.errorMessage, JMAsyncTask.this.id);
            } catch (TimeoutException ex) {
                Logger.getLogger(JMAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
                JMAsyncTask.this.errorMessage=ex.getMessage();
                JMAsyncTask.this.el.onJMError(JMAsyncTask.this.errorMessage, JMAsyncTask.this.id);
            }finally{
                this.executor.shutdown();
            }

        }else{
            this.errorMessage="ERROR.... No Task Event Listener";
            this.el.onJMError(this.errorMessage, this.id);
        }
    }

    public Object getResult(){
        return this.result;
    }

}
