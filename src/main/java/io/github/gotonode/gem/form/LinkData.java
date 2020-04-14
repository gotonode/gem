package io.github.gotonode.gem.form;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LinkData {

    @NotNull
    @NotBlank
    private String address;

    @NotNull
    @NotBlank
    private String key;

    @NotNull
    @Min(0)
    @Max(1024)
    private Integer site;

    @NotNull
    @Min(0)
    @Max(1024)
    private Integer version;
}
