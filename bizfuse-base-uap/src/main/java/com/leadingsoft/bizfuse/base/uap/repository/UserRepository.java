package com.leadingsoft.bizfuse.base.uap.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.leadingsoft.bizfuse.base.uap.model.User;

/**
 * DAO for the User Entity exposing Rest Endpoint
 */
public interface UserRepository extends Repository<User, Long>, UserRepositoryCustom {

    Page<User> findAll(Pageable pageable);

    User findOneByNo(String no);

    User findOneByLoginId(String loginId);

    User findOneByMobile(String mobile);

    User findOneByEmail(String email);

    User findOneByUnionId(String unionId);

    User findOneBySubscriptionOpenId(String openId);

    User findOneByMobileAppOpenId(String openId);

    User findOneByWebsiteAppOpenId(String openId);

    User findOneBySubscriptionOpenIdAndUnionId(String subscriptionOpenId, String unionId);

    User save(User user);

    void delete(Long id);

    List<User> findAllByMobileIn(List<String> mobiles);

}
