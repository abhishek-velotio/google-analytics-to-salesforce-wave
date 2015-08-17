package models.dao.filters;

import java.util.Optional;

/**
 * Class for filter jobs
 * 
 * @author Sergey Legostaev
 *
 */
public class JobFilter extends BaseFilter {
	
	public JobFilter(Integer count, Integer offset, String orderBy, OrderType orderType) {
		this.count = Optional.ofNullable(count);
		this.offset = Optional.ofNullable(offset);
		this.orderBy = Optional.ofNullable(orderBy);
		this.orderType = Optional.ofNullable(orderType);
	}
	
	public JobFilter(Optional<Integer> count, Optional<Integer> page, Optional<String> orderBy, Optional<OrderType> orderType) {
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
