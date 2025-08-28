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

//GDA-02-005
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;

import programmingtheiot.common.ConfigConst;


/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemCpuUtilTask extends BaseSystemUtilTask
{
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemCpuUtilTask()
	{
		super(ConfigConst.NOT_SET, ConfigConst.DEFAULT_TYPE_ID);
	}
	
	
	// NOTE: YOU NEED TO IMPLEMENT THE getTelemetryValue METHOD
	// FROM THE BaseSystemUtilTask ABSTRACT CLASS
	
	// public methods
	// GDA-02-005
	@Override
	public float getTelemetryValue()
	{
		OperatingSystemMXBean mxBean = ManagementFactory.getOperatingSystemMXBean();
		double cpuUtil = mxBean.getSystemLoadAverage();
		
		return (float) cpuUtil;
	}
	
}
