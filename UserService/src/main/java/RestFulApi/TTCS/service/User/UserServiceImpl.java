package RestFulApi.TTCS.service.User;

import RestFulApi.TTCS.dto.request.AddressDTO;
import RestFulApi.TTCS.dto.request.UserRequestDTO;
import RestFulApi.TTCS.dto.response.PageResponse;
import RestFulApi.TTCS.dto.response.UserDetailResponse;
import RestFulApi.TTCS.exception.ResourceNotFoundException;
import RestFulApi.TTCS.model.Address;
import RestFulApi.TTCS.model.User;
import RestFulApi.TTCS.repository.SearchRepository;
import RestFulApi.TTCS.repository.UserRepository;
import RestFulApi.TTCS.repository.criteria.SearchCriteria;
import RestFulApi.TTCS.util.UserStatus;
import RestFulApi.TTCS.util.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final SearchRepository searchRepository;

    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));

        userRepository.save(user);

        log.info("User has save!");

        return user.getId();
    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        if (!request.getEmail().equals(user.getEmail())) {
            user.setEmail(request.getEmail());
        }
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(convertToAddress(request.getAddresses()));
        userRepository.save(user);

        log.info("User updated successfully");
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User changed successfully");
    }

    @Override
    public void deleteUser(long userId) {
        User user = getUserById(userId);
        userRepository.deleteById(userId);
        log.info("User deleted successfully");
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        UserDetailResponse userDetailResponse = UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
        return userDetailResponse;
    }

    @Override
    public PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String... sortBy) {
        List<Sort.Order> sorts = new ArrayList<>();

        for (String s: sortBy) {
            if(StringUtils.hasLength(s)){
//            firstName:asc , firsName:Decs
                Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    if(matcher.group(3).equalsIgnoreCase("asc")){
                        sorts.add(new Sort.Order(Sort.Direction.ASC , matcher.group(1)));
                    } else if (matcher.group(3).equalsIgnoreCase("desc")){
                        sorts.add(new Sort.Order(Sort.Direction.DESC , matcher.group(1)));

                    }
                }
            }
        }


//        Sort.Direction.DESC , fied
        Pageable pageable = PageRequest.of(pageNo , pageSize , Sort.by(sorts));
        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailResponse> userDetailResponses = users.stream().map(user -> UserDetailResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).
                toList();

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(users.getTotalPages())
                .items(userDetailResponses)
                .build();
    }

    @Override
    public PageResponse<?> getAllUserMultipleColumnsAndSearch(int pageNo, int pageSize, String search,String  sortBy ) {
        return searchRepository.searchUser(pageNo , pageSize , search , sortBy );

    }

    @Override
    public PageResponse<?>  advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search ) {


        return searchRepository.avancedSearchUser(pageNo , pageSize , sortBy ,address,search );

    }

//    @Override
//    public List<UserDetailResponse> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
////        sortBy = field
//
//
//        List<Sort.Order> sorts = new ArrayList<>();
//        if(StringUtils.hasLength(sortBy)){
////            firstName:asc , firsName:Decs
//            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
//            Matcher matcher = pattern.matcher(sortBy);
//            if(matcher.find()){
//                if(matcher.group(3).equalsIgnoreCase("asc")){
//                    sorts.add(new Sort.Order(Sort.Direction.ASC , matcher.group(1)));
//                } else if (matcher.group(3).equalsIgnoreCase("desc")){
//                    sorts.add(new Sort.Order(Sort.Direction.DESC , matcher.group(1)));
//
//                }
//            }
//        }

//        Sort.Direction.DESC , fied
//        Pageable pageable = PageRequest.of(pageNo , pageSize , Sort.by(sorts));
//        Page<User> users = userRepository.findAll(pageable);
//        return users.stream().map(user -> UserDetailResponse.builder()
//                        .id(user.getId())
//                        .firstName(user.getFirstName())
//                        .lastName(user.getLastName())
//                        .email(user.getEmail())
//                        .build())
//                .toList();
//    }


    private User getUserById(long userId){
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found "+userId));
    }

    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
        Set<Address> result = new HashSet<>();
        addresses.forEach(a ->
                result.add(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build())
        );
        return result;
    }
}
