package com.leadingsoft.bizfuse.base.uap.dto.convertor;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.leadingsoft.bizfuse.base.uap.dto.UserDTO;
import com.leadingsoft.bizfuse.base.uap.model.User;
import com.leadingsoft.bizfuse.base.uap.repository.UserRepository;
import com.leadingsoft.bizfuse.common.web.dto.AbstractConvertor;
import com.leadingsoft.bizfuse.common.web.utils.encode.PasswordEncoder;

@Component
public class UserConvertor extends AbstractConvertor<User, UserDTO> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User toModel(@NotNull final UserDTO dto) {
        if (dto.isNew()) {// 用户新增操作
            return this.newModel(dto);
        } else {// 用户修改操作
            return this.updateModel(dto);
        }
    }

    private User updateModel(final UserDTO dto) {
        final User model = this.userRepository.findOneByNo(dto.getNo());
        model.setWeChatId(dto.getWeChatId());
        model.getDetails().setName(dto.getName());
        model.getDetails().setNickname(dto.getNickname());
        model.getDetails().setBirthday(dto.getBirthday());
        model.getDetails().setGender(dto.getGender());
        model.getDetails().setCountry(dto.getCountry());
        model.getDetails().setProvince(dto.getProvince());
        model.getDetails().setCity(dto.getCity());
        model.getDetails().setDistrict(dto.getDistrict());
        model.getDetails().setAddress(dto.getAddress());
        model.setEnabled(dto.getEnabled());

        return model;
    }

    private User newModel(final UserDTO dto) {
        final User model = new User();
        model.setLoginId(dto.getLoginId());
        model.setMobile(dto.getMobile());
        model.setEmail(dto.getEmail());
        model.setPassword(this.passwordEncoder.encode(dto.getPassword()));
        model.setWeChatId(dto.getWeChatId());
        model.getDetails().setName(dto.getName());
        model.getDetails().setNickname(dto.getNickname());
        model.getDetails().setBirthday(dto.getBirthday());
        model.getDetails().setGender(dto.getGender());
        model.getDetails().setCountry(dto.getCountry());
        model.getDetails().setProvince(dto.getProvince());
        model.getDetails().setCity(dto.getCity());
        model.getDetails().setDistrict(dto.getDistrict());
        model.getDetails().setAddress(dto.getAddress());
        model.setEnabled(dto.getEnabled());
        return model;
    }

    @Override
    public UserDTO toDTO(@NotNull final User model, final boolean forListView) {
        final UserDTO dto = new UserDTO();

        dto.setId(model.getId());
        dto.setNo(model.getNo());
        dto.setLoginId(model.getLoginId());
        dto.setMobile(model.getMobile());
        dto.setEmail(model.getEmail());
        // dto.setPassword(model.getPassword()); 密码不返给客户端
        dto.setName(model.getDetails().getName());
        dto.setNickname(model.getDetails().getNickname());
        dto.setBirthday(model.getDetails().getBirthday());
        dto.setGender(model.getDetails().getGender());
        dto.setCountry(model.getDetails().getCountry());
        dto.setProvince(model.getDetails().getProvince());
        dto.setCity(model.getDetails().getCity());
        dto.setDistrict(model.getDetails().getDistrict());
        dto.setAddress(model.getDetails().getAddress());
        dto.setWeChatId(model.getWeChatId());
        dto.setUnionId(model.getUnionId());
        dto.setSubscriptionOpenId(model.getSubscriptionOpenId());
        dto.setMobileAppOpenId(model.getMobileAppOpenId());
        dto.setWebsiteAppOpenId(model.getWebsiteAppOpenId());
        dto.setEnabled(model.isEnabled());
        dto.setAccountLocked(model.isAccountLocked());
        dto.setAccountExpired(model.isAccountExpired());
        dto.setCredentialsExpired(model.isCredentialsExpired());

        this.loadAuditToDTO(model, dto);

        return dto;
    }

}
