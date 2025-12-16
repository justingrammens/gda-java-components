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

package programmingtheiot.gda.connection;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * Shell representation of class for student implementation.
 * 
 */
public class RedisPersistenceAdapter implements IPersistenceClient
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(RedisPersistenceAdapter.class.getName());
	
	// private var's
	private String host = null;
    private int port = 0;
    private String password = null;
    private Jedis redisClient = null;
    private boolean isConnected = false;
	
	
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public RedisPersistenceAdapter()
	{
		super();
		
		initConfig();
	}
	
	
	// public methods
	
	// public methods
	
	/**
	 *
	 */
	@Override
	public boolean connectClient()
	{
		if (isConnected) {
            _Logger.warning("Redis client already connected.");
            return true;
        }
        try {
            this.redisClient = new Jedis(this.host, this.port);
            
            // Authenticate if password is provided
            if (this.password != null && !this.password.isEmpty()) {
                _Logger.info("Authenticating to Redis with password...");
                this.redisClient.auth(this.password);
            }
            
            // Test the connection with a PING
            String response = this.redisClient.ping();
            if ("PONG".equals(response)) {
                this.isConnected = true;
                _Logger.info("Connected to Redis at " + host + ":" + port);
            } else {
                this.isConnected = false;
                _Logger.severe("Redis connection test failed");
            }
        } catch (JedisConnectionException e) {
            _Logger.severe("Failed to connect to Redis at " + host + ":" + port + ". Is Redis running? " + e.getMessage());
            this.isConnected = false;
            if (this.redisClient != null) {
                try {
                    this.redisClient.close();
                } catch (Exception ex) {
                    // Ignore close errors
                }
                this.redisClient = null;
            }
        } catch (Exception e) {
            _Logger.severe("Failed to connect to Redis: " + e.getMessage());
            this.isConnected = false;
            if (this.redisClient != null) {
                try {
                    this.redisClient.close();
                } catch (Exception ex) {
                    // Ignore close errors
                }
                this.redisClient = null;
            }
        }
        return this.isConnected;
	}

	/**
	 *
	 */
	@Override
	public boolean disconnectClient()
	{
		if (!isConnected) {
            _Logger.warning("Redis client already disconnected.");
            return true;
        }
        try {
            this.redisClient.close();
            this.isConnected = false;
            _Logger.info("Disconnected from Redis.");
        } catch (Exception e) {
            _Logger.severe("Failed to disconnect from Redis: " + e.getMessage());
        }
        return !this.isConnected;
	}
	
	 public boolean isClientConnected() {
	        return this.isConnected;
	 }
	 

	/**
	 *
	 */
	@Override
	public ActuatorData[] getActuatorData(String topic, Date startDate, Date endDate)
	{
		return new ActuatorData[0];
	}

	/**
	 *
	 */
	@Override
	public SensorData[] getSensorData(String topic, Date startDate, Date endDate)
	{
		return new SensorData[0];
	}

	/**
	 *
	 */
	@Override
	public void registerDataStorageListener(Class cType, IPersistenceListener listener, String... topics)
	{
	}

	/**
	 *
	 */
	@Override
	public boolean storeData(String topic, int qos, ActuatorData... data)
	{
		if (!isClientConnected() || data == null) {
			_Logger.warning("Cannot store ActuatorData - Redis client not connected or data is null");
			return false;
		}
		try {
			var du = DataUtil.getInstance();
			for (ActuatorData d : data) {
				String json = du.actuatorDataToJson(d);
				this.redisClient.rpush(topic, json);
			}
			return true;
		} catch (JedisConnectionException e) {
			_Logger.severe("Lost connection to Redis while storing ActuatorData: " + e.getMessage());
			this.isConnected = false;
			return false;
		} catch (Exception e) {
			_Logger.severe("Error storing ActuatorData to Redis: " + e.getMessage());
			return false;
		}
	}

	/**
	 *
	 */
	@Override
	public boolean storeData(String topic, int qos, SensorData... data)
	{
		if (!isClientConnected() || data == null) {
			_Logger.warning("Cannot store SensorData - Redis client not connected or data is null");
			return false;
		}
		try {
			var du = DataUtil.getInstance();
			for (SensorData d : data) {
				String json = du.sensorDataToJson(d);
				this.redisClient.rpush(topic, json);
			}
			return true;
		} catch (JedisConnectionException e) {
			_Logger.severe("Lost connection to Redis while storing SensorData: " + e.getMessage());
			this.isConnected = false;
			return false;
		} catch (Exception e) {
			_Logger.severe("Error storing SensorData to Redis: " + e.getMessage());
			return false;
		}
	}

	/**
	 *
	 */
	@Override
	public boolean storeData(String topic, int qos, SystemPerformanceData... data)
	{
		if (!isClientConnected() || data == null) {
			_Logger.warning("Cannot store SystemPerformanceData - Redis client not connected or data is null");
			return false;
		}
		try {
			var du = DataUtil.getInstance();
			for (SystemPerformanceData d : data) {
				String json = du.systemPerformanceDataToJson(d);
				this.redisClient.rpush(topic, json);
			}
			return true;
		} catch (JedisConnectionException e) {
			_Logger.severe("Lost connection to Redis while storing SystemPerformanceData: " + e.getMessage());
			this.isConnected = false;
			return false;
		} catch (Exception e) {
			_Logger.severe("Error storing SystemPerformanceData to Redis: " + e.getMessage());
			return false;
		}
	}
	
	// private methods
	
	/**
	 * 
	 */
	private void initConfig()
	{
		ConfigUtil config = ConfigUtil.getInstance();
    	this.host = config.getProperty(ConfigConst.DATA_GATEWAY_SERVICE, ConfigConst.HOST_KEY, "localhost");
    	this.port = config.getInteger(ConfigConst.DATA_GATEWAY_SERVICE, ConfigConst.PORT_KEY, 6379);
    	
    	// Load password from credentials file if available
    	try {
    		Properties credProps = config.getCredentials(ConfigConst.DATA_GATEWAY_SERVICE);
    		if (credProps != null) {
    			this.password = credProps.getProperty(ConfigConst.USER_AUTH_TOKEN_KEY);
    			if (this.password != null && !this.password.isEmpty()) {
    				_Logger.info("Redis authentication token loaded from credentials file.");
    			} else {
    				_Logger.warning("Credentials file found but '" + ConfigConst.USER_AUTH_TOKEN_KEY + "' key is missing or empty.");
    				_Logger.info("Available keys in credentials: " + credProps.keySet());
    			}
    		} else {
    			_Logger.warning("No credentials returned for section: " + ConfigConst.DATA_GATEWAY_SERVICE);
    		}
    	} catch (Exception e) {
    		_Logger.log(Level.WARNING, "Error loading Redis credentials: " + e.getMessage(), e);
    		this.password = null;
    	}
	}
	

}
