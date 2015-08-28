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
 * Class for filter jobs
 * 
 * @author Sergey Legostaev
 *
 */
public class JobFilter extends BaseFilter {
	
	public JobFilter(Integer count, Integer offset, String orderBy, BaseFilter.OrderType orderType) {
		this.count = Optional.ofNullable(count);
		this.offset = Optional.ofNullable(offset);
		this.orderBy = Optional.ofNullable(orderBy);
		this.orderType = Optional.ofNullable(orderType);
	}
	
	public JobFilter(Optional<Integer> count, Optional<Integer> page, Optional<String> orderBy, Optional<BaseFilter.OrderType> orderType) {
		this.count = count;
		this.offset = Optional.of(page.orElse(1) <= 0 ? DEFAULT_OFFSET : (page.orElse(1) - 1) * count.orElse(DEFAULT_COUNT));
		this.orderBy = orderBy;
		this.orderType = orderType;
	}
	
//	private Optional<Integer> calculateOffset(Optional<Integer> count, Optional<Integer> page) {
//		Optional<Integer> tmp = Optional.of(page.orElse(1) < 0 ? DEFAULT_OFFSET : (page.orElse(1) - 1) * count.orElse(DEFAULT_COUNT));
//		return Optional.of(tmp.get() == 0 ? 0 : tmp.get() - 1);
//	}
}
