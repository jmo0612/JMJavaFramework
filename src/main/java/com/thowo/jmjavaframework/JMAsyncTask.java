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

public class JMAsyncTask {
    private JMAsyncListener el=null;
    private String errorMessage="";
    private Object result=null;
    private Integer timeOut=1;
    private String id="";
    
    public JMAsyncTask(JMAsyncListener el, Callable<?> task, String id){
        this.el=el;
        this.id=id;
        process(task);
    }
    
    public JMAsyncTask(JMAsyncListener el, Callable<?> task, Integer timeOut){
        this.el=el;
        this.id=id;
        this.timeOut=timeOut;
        new Thread(new Runnable(){
            @Override
            public void run() {
                JMAsyncTask.this.process(task);
            }
        }).start();
    }
    
    private void process(Callable<?> task){
        if(this.el!=null){
            this.el.onJMStart(this.id);
            ExecutorService executor=Executors.newFixedThreadPool(1);
            Future<?> future=executor.submit(task);
            try {
                this.el.onJMProcess(this.id);
                this.result=future.get(1, TimeUnit.MINUTES);
                this.el.onJMComplete(this.result, this.id);
            } catch (InterruptedException ex) {
                Logger.getLogger(JMAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
                this.errorMessage=ex.getMessage();
                this.el.onJMError(this.errorMessage, this.id);
            } catch (ExecutionException ex) {
                Logger.getLogger(JMAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
                this.errorMessage=ex.getMessage();
                this.el.onJMError(this.errorMessage, this.id);
            } catch (TimeoutException ex) {
                Logger.getLogger(JMAsyncTask.class.getName()).log(Level.SEVERE, null, ex);
                this.errorMessage=ex.getMessage();
                this.el.onJMError(this.errorMessage, this.id);
            }finally{
                executor.shutdown();
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
