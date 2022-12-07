package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class OfficeTimeRequest extends BasicRequest{

    private Long officeTimeId;

    @Pattern(regexp = "(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "timeStart wrong")
    @NotNull(message = "timeStart null")
    private String timeStart;

    @Pattern(regexp = "(2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]", message = "Invalid timeFinish")
    private String timeFinish;

    private Date createDate;

    @JsonCreator
    public OfficeTimeRequest(String timeStart, String timeFinish){
        this.timeStart = timeStart;
        this.timeFinish = timeFinish;
    }
}
