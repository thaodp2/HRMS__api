package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.repsotories.RequestTypeRepository;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SqlResultSetMapping(name = RequestDto.SQL_RESULT_SET_MAPPING,
        classes = @ConstructorResult(
                targetClass = RequestDto.class,
                columns = {
                        @ColumnResult(name = "request_id", type = Integer.class),
                        @ColumnResult(name = "request_type_name", type = String.class),
                        @ColumnResult(name = "full_name", type = String.class),
                        @ColumnResult(name = "start_time", type = String.class),
                        @ColumnResult(name = "end_time", type = String.class),
                        @ColumnResult(name = "reason", type = String.class),
                        @ColumnResult(name = "create_date", type = String.class),
                        @ColumnResult(name = "status", type = String.class),
                }))
public class RequestDto {
    public static final String SQL_RESULT_SET_MAPPING = "RequestDto";

    @Id
    private Integer requestId;

    private String requestType;

    private String personName;

    private String startTime;

    private String endTime;

    private String reason;

    private String createDate;

    private String status;

}
