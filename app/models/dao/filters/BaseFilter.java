package models.dao.filters;

import java.util.Optional;

/**
 * 
 * @author Sergey Legostaev
 *
 */

public abstract class BaseFilter {
	
	public enum OrderType {
		asc, desc;
	}
	
	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_COUNT = 10;
	public static final String DEFAULT_ORDER_BY = "created";
	public static final OrderType DEFAULT_ORDER_TYPE = OrderType.desc;
	
	public Optional<Integer> count;
	public Optional<Integer> offset;
	public Optional<String> orderBy;
	public Optional<OrderType> orderType; 
	
}
