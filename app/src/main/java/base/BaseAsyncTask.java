package base;

import android.os.AsyncTask;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result>
{
	public void multithreadExecute(Params... requestData) {
		if (android.os.Build.VERSION.SDK_INT >= 11)
        {	
        	try
        	{
	        	Class<? extends Object> execType = Class.forName("java.util.concurrent.Executor");
	        	Class<? extends Object> tatType = getClass();
	        	
	        	Field poolExecutorField = tatType.getField("THREAD_POOL_EXECUTOR");
	        	Object poolExecutor = poolExecutorField.get(this);
	        	
	        	Method m = tatType.getMethod("executeOnExecutor",execType,Object[].class);
	        	m.invoke(this, poolExecutor, requestData);
        	}
        	catch(Exception e)
        	{
        	}
        }
		else
		{
			super.execute(requestData);
		}
	}
}
