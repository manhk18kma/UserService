package RestFulApi.TTCS.repository.criteria;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SearchCriteria {
    private String key; //field
    private String operation; // toan tu
    private Object value;
}
