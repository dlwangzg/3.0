package com.leadingsoft.bizfuse.quartz.convertor;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.leadingsoft.bizfuse.quartz.dto.PageData;
import com.leadingsoft.bizfuse.quartz.dto.ResultDTO;
import com.leadingsoft.bizfuse.quartz.dto.ResultDTO.Status;

/**
 * 基础Model与DTO转换类
 *
 * @author liuyg
 */
public abstract class AbstractConvertor<Model, DTO> {

    /**
     * 将 DTO 转换为 Model.
     * <p>
     * 适用于新建或修改 Model 的操作, 可通过 dto 中的 isNew() 方法来判断将要进行的操作.
     *
     * @param dto
     * @return
     */
    public abstract Model toModel(@NotNull final DTO dto);

    /**
     * 将 Model 转换为 DTO.
     * <p>
     *
     * @param model
     * @return
     */
    public abstract DTO toDTO(@NotNull final Model model);

    public final List<Model> toListModel(@NotEmpty final Collection<DTO> collector) {
        return collector.stream().map(dto -> this.toModel(dto)).collect(Collectors.toList());
    }

    public final List<DTO> toListDTO(@NotEmpty final Collection<Model> collector) {
        return collector.stream().map(model -> this.toDTO(model)).collect(Collectors.toList());
    }

    public final List<DTO> toListDTO(@NotNull final Page<Model> modelPage) {
        return this.toListDTO(modelPage.getContent());
    }

    public final Page<DTO> toPageDTO(@NotNull final Page<Model> modelPage, @NotNull final Pageable pageable) {
        return new PageImpl<>(this.toListDTO(modelPage), pageable, modelPage.getTotalElements());
    }

    public final ResultDTO<DTO> toResultDTO(@NotNull final Model model) {
        final ResultDTO<DTO> result = new ResultDTO<>();
        if (model != null) {
            final DTO dto = this.toDTO(model);
            result.setData(dto);
        }
        result.setStatus(Status.success);
        return result;
    }

    public final ResultDTO<List<DTO>> toResultDTO(@NotNull final Collection<Model> models) {
        final List<DTO> dtos = this.toListDTO(models);
        final ResultDTO<List<DTO>> result = new ResultDTO<>();
        result.setData(dtos);
        result.setStatus(Status.success);
        return result;
    }

    public final ResultDTO<List<DTO>> toResultDTO(@NotNull final Page<Model> models) {
        final List<DTO> dtos = this.toListDTO(models);
        final ResultDTO<List<DTO>> result = new ResultDTO<>();
        final PageData pageData = PageData.convert(models);
        result.setData(dtos);
        result.setPageable(pageData);
        result.setStatus(Status.success);
        return result;
    }
}
