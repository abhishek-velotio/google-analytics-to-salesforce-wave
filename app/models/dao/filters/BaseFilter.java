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

public abstract class BaseFilter {
	
	public enum OrderType {
		asc, desc;
	}
	
	public static final int DEFAULT_OFFSET = 0;
	public static final int DEFAULT_COUNT = 1000;
	public static final String DEFAULT_ORDER_BY = "created";
	public static final OrderType DEFAULT_ORDER_TYPE = OrderType.desc;
	
	public Optional<Integer> count;
	public Optional<Integer> offset;
	public Optional<String> orderBy;
	public Optional<OrderType> orderType; 
	
}
