/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */

package programmingtheiot.gda.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SystemPerformanceData;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemPerformanceManager
{
	// GDA-02-007
	private ScheduledExecutorService schedExecSvc = null;
	private SystemCpuUtilTask sysCpuUtilTask = null;
	private SystemMemUtilTask sysMemUtilTask = null;

	private Runnable taskRunner = null;
	private boolean isStarted = false;
	
	// GDA-02-002
	private static final Logger _Logger = Logger.getLogger(SystemPerformanceManager.class.getName());
	private int pollRate = ConfigConst.DEFAULT_POLL_CYCLES;
	
	private String locationID = ConfigConst.NOT_SET;
	private IDataMessageListener dataMsgListener = null;
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceManager()
	{
		this.pollRate =
			ConfigUtil.getInstance().getInteger(
				ConfigConst.GATEWAY_DEVICE, ConfigConst.POLL_CYCLES_KEY, ConfigConst.DEFAULT_POLL_CYCLES);
		
		this.locationID =
				ConfigUtil.getInstance().getProperty(
					ConfigConst.GATEWAY_DEVICE, ConfigConst.LOCATION_ID_PROP, ConfigConst.NOT_SET);
		
		if (this.pollRate <= 0) {
			this.pollRate = ConfigConst.DEFAULT_POLL_CYCLES;
		}
		
		this.schedExecSvc   = Executors.newScheduledThreadPool(1);
		this.sysCpuUtilTask = new SystemCpuUtilTask();
		this.sysMemUtilTask = new SystemMemUtilTask();
		
		this.taskRunner = () -> {
			this.handleTelemetry();
		};
	}
	
	
	// public methods
	
	public void handleTelemetry()
	{
		float cpuUtil = this.sysCpuUtilTask.getTelemetryValue();
		float memUtil = this.sysMemUtilTask.getTelemetryValue();
		
		// NOTE: you may need to change the logging level to 'info' to see the message
		//_Logger.fine("CPU utilization: " + cpuUtil + ", Mem utilization: " + memUtil);
		_Logger.log(Level.INFO, "CPU utilization: {0}, Mem utilization: {1}", new Object[]{cpuUtil, memUtil});
		
		SystemPerformanceData spd = new SystemPerformanceData();
		spd.setLocationID(this.locationID);
		spd.setCpuUtilization(cpuUtil);
		spd.setMemoryUtilization(memUtil);
		
		if (this.dataMsgListener != null) {
			this.dataMsgListener.handleSystemPerformanceMessage(
				ResourceNameEnum.GDA_SYSTEM_PERF_MSG_RESOURCE, spd);
		}
	}
	
	public void setDataMessageListener(IDataMessageListener listener)
	{
		if (listener != null) {
			this.dataMsgListener = listener;
		}
	}
	
	// GDA-02-002
	// GDA-02-007
	public boolean startManager()
	{
		if (! this.isStarted) {
			_Logger.info("SystemPerformanceManager is starting...");
			
			ScheduledFuture<?> futureTask =
				this.schedExecSvc.scheduleAtFixedRate(this.taskRunner, 1L, this.pollRate, TimeUnit.SECONDS);
			
			this.isStarted = true;
		} else {
			_Logger.info("SystemPerformanceManager is already started.");
		}
		
		return this.isStarted;
	}

	// GDA-02-002
	// GDA-02-007
	public boolean stopManager()
	{
		this.schedExecSvc.shutdown();
		this.isStarted = false;
		
		_Logger.info("SystemPerformanceManager is stopped.");
		
		return true;
	}
	
}
