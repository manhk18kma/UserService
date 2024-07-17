package RestFulApi.TTCS.service.User;

import RestFulApi.TTCS.dto.request.UserRequestDTO;
import RestFulApi.TTCS.dto.response.PageResponse;
import RestFulApi.TTCS.dto.response.UserDetailResponse;
import RestFulApi.TTCS.util.UserStatus;

import java.util.List;

public interface UserService {
    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String ...sortBy);


    PageResponse<?> getAllUserMultipleColumnsAndSearch(int pageNo, int pageSize,  String search,String sortBy);

    PageResponse<?>  advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy,  String address, String... search);

//
//    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);
//
//    PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy);
//
//    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);
//
//    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);

}
