/**
 * This document is a part of the source code and related artifacts
 * for GA2SA, an open source code for Google Analytics to 
 * Salesforce Analytics integration.
 *
 * Copyright © 2015 Cervello Inc.,
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package models.dao.filters;

import java.util.Optional;

/**
 * 
 * @author Sergey Legostaev
 *
 */

public class BaseFilter<T> {
	
	public enum OrderType {
		asc, desc;
	}
	
	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_COUNT = 1000;
	public static final String DEFAULT_ORDER_BY = "created";
	public static final OrderType DEFAULT_ORDER_TYPE = OrderType.desc;
	
	public Optional<Integer> count = Optional.empty();
	public Optional<Integer> offset = Optional.empty();
	public Optional<String> orderBy = Optional.empty();
	public Optional<OrderType> orderType = Optional.empty();
	public Optional<Class<T>> objClass = Optional.empty();
	
	public BaseFilter() {}
	
	public BaseFilter(Class<T> objClass) { 
		this.objClass = Optional.ofNullable(objClass);
	}
	
	public BaseFilter(Integer count, Integer page, String orderBy, BaseFilter.OrderType orderType, Class<T> objClass) {
		this(objClass);
		this.count = Optional.ofNullable(count);
		this.offset = getOffset(Optional.ofNullable(page));
		this.orderBy = Optional.ofNullable(orderBy);
		this.orderType = Optional.ofNullable(orderType);
		
	}
	
	public BaseFilter(Optional<Integer> count, Optional<Integer> page, Optional<String> orderBy, Optional<BaseFilter.OrderType> orderType) {
		this.count = count;
		this.offset = getOffset(page);
		this.orderBy = orderBy;
		this.orderType = orderType;
	}
	
	private Optional<Integer> getOffset(Optional<Integer> page) {
		return Optional.of(page.orElse(1) <= 0 ? DEFAULT_OFFSET : (page.orElse(1) - 1) * count.orElse(DEFAULT_COUNT));
	}
	
}
