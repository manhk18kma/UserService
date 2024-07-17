package RestFulApi.TTCS.repository;

import RestFulApi.TTCS.dto.response.PageResponse;
import RestFulApi.TTCS.dto.response.UserDetailResponse;
import RestFulApi.TTCS.model.Address;
import RestFulApi.TTCS.model.User;
import RestFulApi.TTCS.repository.criteria.SearchCriteria;
import RestFulApi.TTCS.repository.criteria.UserSearchQueryCriteriaConsumer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
@Slf4j
//@Repository
public class SearchRepository {
    @PersistenceContext
    private EntityManager entityManager;


    public PageResponse<?> searchUser(int pageNo, int pageSize, String search, String sortBy ) {
        log.info("Execute search user with keyword={}", search);
        StringBuilder sqlQuery = new StringBuilder("SELECT new RestFulApi.TTCS.dto.response.UserDetailResponse(u.id, u.firstName, u.lastName, u.phone, u.email) FROM User u WHERE 1=1");

//        StringBuilder sqlQuery = new StringBuilder("select u from User u where 1 = 1");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" AND lower(u.firstName) like lower(:firstName)");
            sqlQuery.append(" OR lower(u.lastName) like lower(:lastName)");
            sqlQuery.append(" OR lower(u.email) like lower(:email)");
        }

        if (StringUtils.hasLength(sortBy)) {
           Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
           Matcher matcher = pattern.matcher(sortBy);
           if(matcher.find()){
               sqlQuery.append(String.format(" order by u.%s %s", matcher.group(1) , matcher.group(3)));
           }

        }
        // Get list of users
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter("firstName", String.format("%%%s%%",search));
            selectQuery.setParameter("lastName", String.format("%%%s%%",search));
            selectQuery.setParameter("email", String.format("%%%s%%",search));
        }
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        List<?> users = selectQuery.getResultList();
        StringBuilder sqlCountQuery = new StringBuilder("SELECT COUNT(*) FROM User u");
        if (StringUtils.hasLength(search)) {
            sqlCountQuery.append(" WHERE lower(u.firstName) like lower(?1)");
            sqlCountQuery.append(" OR lower(u.lastName) like lower(?2)");
            sqlCountQuery.append(" OR lower(u.email) like lower(?3)");
        }

        Query countQuery = entityManager.createQuery(sqlCountQuery.toString());
        if (StringUtils.hasLength(search)) {
            countQuery.setParameter(1, String.format("%%%s%%", search));
            countQuery.setParameter(2, String.format("%%%s%%", search));
            countQuery.setParameter(3, String.format("%%%s%%", search));
            countQuery.getSingleResult();
        }
        Long totalElements = (Long) countQuery.getSingleResult();
        log.info("totalElements={}", totalElements);


//        List<UserDetailResponse> userDetailResponses = users.stream()
//                .map(u -> UserDetailResponse.builder()
//                        .id(u.getId())
//                        .firstName(u.getFirstName())
//                        .lastName(u.getLastName())
//                        .phone(u.getPhone())
//                        .email(u.getEmail())
//                        .build())
//                .collect(Collectors.toList());
//        Page<UserDetailResponse> page = new PageImpl<>(userDetailResponses, PageRequest.of(pageNo, pageSize), totalElements);

        Page<?> page = new PageImpl<>(users, PageRequest.of(pageNo, pageSize), totalElements);
//        page.stream().forEach(System.out::println);
//        page.getContent().forEach(System.out::println);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPage(page.getTotalPages())
                .items(page.stream().toList())
                .build();
    }

    public PageResponse<?> avancedSearchUser(int pageNo, int pageSize, String sortBy,  String address,String... search){
        List<SearchCriteria> criteriaList = new ArrayList<>();
        if(search!=null){

            for (String s: search) {
                if(StringUtils.hasLength(s)){
//            firstName:value
                    Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(.*)");
                    Matcher matcher = pattern.matcher(s);
                    if(matcher.find()){
                        criteriaList.add(new SearchCriteria(matcher.group(1) , matcher.group(2), matcher.group(3)));
                    }
                }
            }
        }

        List<User> users = getUsers(pageNo , pageSize , criteriaList ,sortBy ,address);
        Long totalElement = getTotalElements(criteriaList , address);
        Page<User> page = new PageImpl<>(users, PageRequest.of(pageNo, pageSize), totalElement);

        return PageResponse.builder()
                .pageNo(pageNo) ////        offset = vi tri bat dau
                .pageSize(pageSize)
                .totalPage(page.getTotalPages()) //tat ca phan tu
                .items(users)
                .build();
    }


//    private List<User> getUsers(int pageNo , int pageSize , List<SearchCriteria> criteriaList ,  String sortBy , String address){
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
//        Root<User> root = query.from(User.class);
//
//
//        Predicate predicate = criteriaBuilder.conjunction();
//        UserSearchQueryCriteriaConsumer queryCriteriaConsumer = new UserSearchQueryCriteriaConsumer(
//                predicate, criteriaBuilder,root
//        );
//
//        if (StringUtils.hasLength(address)){
//            log.info("if");
//            for (SearchCriteria criteria : criteriaList) {
//                queryCriteriaConsumer.accept(criteria);
//            }
//            predicate = queryCriteriaConsumer.getPredicate();
//            Join<Address , User> addressUserJoin = root.join("addresses");
//            Predicate addressPredicate = criteriaBuilder.like(addressUserJoin.get("city") , "%"+address+"%");
//            query.where(predicate , addressPredicate);
//        }else {
////        criteriaList.forEach(queryCriteriaConsumer);
//            log.info("else");
//            for (SearchCriteria criteria : criteriaList) {
//                queryCriteriaConsumer.accept(criteria);
//            }
//            predicate = queryCriteriaConsumer.getPredicate();
//            query.where(predicate);
//        }
//
//        if (StringUtils.hasLength(sortBy)) {
//            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
//            Matcher matcher = pattern.matcher(sortBy);
//            if(matcher.find()){
//                String columnName  = matcher.group(1);
//                if(matcher.group(3).equalsIgnoreCase("desc")){
//                    query.orderBy(criteriaBuilder.desc(root.get(columnName)));
//                }else {
//                    query.orderBy(criteriaBuilder.asc(root.get(columnName)));
//                }
//            }
//
//        }
//        return entityManager.createQuery(query).setFirstResult(pageNo).setMaxResults(pageSize).getResultList();
//
//    }




//Cach 2
    private List<User> getUsers(int offset, int pageSize, List<SearchCriteria> criteriaList, String sortBy ,String address) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
        Root<User> userRoot = query.from(User.class);

        Predicate userPredicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(userPredicate, criteriaBuilder, userRoot);

        if (StringUtils.hasLength(address)) {
            criteriaList.forEach(searchConsumer);
            userPredicate = searchConsumer.getPredicate();
            Join<Address, User> userAddressJoin = userRoot.join("addresses");
            Predicate addressPredicate = criteriaBuilder.equal(userAddressJoin.get("city"), address);
            query.where(userPredicate, addressPredicate);
        } else {
            criteriaList.forEach(searchConsumer);
            userPredicate = searchConsumer.getPredicate();
            query.where(userPredicate);
        }

        Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
        if (StringUtils.hasLength(sortBy)) {
            Matcher matcher = pattern.matcher(sortBy);
            if (matcher.find()) {
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);
                if (direction.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(userRoot.get(fieldName)));
                } else {
                    query.orderBy(criteriaBuilder.desc(userRoot.get(fieldName)));
                }
            }
        }

        return entityManager.createQuery(query)
                .setFirstResult(offset)
                .setMaxResults(pageSize)
                .getResultList();
    }

    private Long getTotalElements(List<SearchCriteria> criteriaList, String address) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class); // Chú ý kiểu dữ liệu ở đây là Long
        Root<User> userRoot = query.from(User.class);

        Predicate userPredicate = criteriaBuilder.conjunction();
        UserSearchQueryCriteriaConsumer searchConsumer = new UserSearchQueryCriteriaConsumer(userPredicate, criteriaBuilder, userRoot);

        if (StringUtils.hasLength(address)) {
            criteriaList.forEach(searchConsumer);
            userPredicate = searchConsumer.getPredicate();
            Join<Address, User> userAddressJoin = userRoot.join("addresses");
            Predicate addressPredicate = criteriaBuilder.equal(userAddressJoin.get("city"), address);
            query.where(userPredicate, addressPredicate);
        } else {
            criteriaList.forEach(searchConsumer);
            userPredicate = searchConsumer.getPredicate();
            query.where(userPredicate);
        }

        // Sử dụng count() để đếm số lượng phần tử
        query.select(criteriaBuilder.count(userRoot));

        return entityManager.createQuery(query).getSingleResult(); // Sử dụng getSingleResult() để lấy kết quả đếm
    }

}
