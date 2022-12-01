package com.minswap.hrms.service.officeTime;

import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.entities.OfficeTime;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.OfficeTimeRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.request.OfficeTimeRequest;
import com.minswap.hrms.response.OfficeTimeResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OfficeTimeServiceImpl implements OfficeTimeService{

    private static final long MILLISECOND_7_HOURS = 7 * 60 * 60 * 1000;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    OfficeTimeRepository officeTimeRepository;
    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateOfficeTime(OfficeTimeRequest officeTimeRequest) throws Exception {
        try{
        ModelMapper modelMapper = new ModelMapper();
        Long officeTimeId = officeTimeRepository.getPresentOfficeTimeId();
        Optional<OfficeTime> officeTimeDB = officeTimeRepository.findOfficeTimeByOfficeTimeId(officeTimeId);

        if(!officeTimeDB.isPresent()){
            throw new Exception("OfficeTime not exist");
        }

        Long personId = officeTimeDB.get().getPersonId();
        officeTimeRequest.setOfficeTimeId(officeTimeDB.get().getOfficeTimeId());
        Instant instant = Instant.now();
        officeTimeRequest.setCreateDate(Date.from(instant));
        OfficeTime officeTime = officeTimeDB.get();
        if (officeTimeRequest.getTimeStart() == null){
            officeTimeRequest.setTimeStart(officeTime.getTimeStart());
        }
        if(officeTimeRequest.getTimeFinish() == null){
            officeTimeRequest.setTimeFinish(officeTime.getTimeFinish());
        }
        java.util.Date dateAdd = officeTimeRequest.getCreateDate();
        dateAdd.setTime(dateAdd.getTime() + MILLISECOND_7_HOURS);
            officeTimeRequest.setCreateDate(dateAdd);
        modelMapper.map(officeTimeRequest, officeTime);
        officeTime.setPersonId(personId);
        officeTimeRepository.save(officeTime);

        List<Long> personIds = personRepository.getAllPersonId();
        for (Long item: personIds) {
            Notification notification = new Notification("Office time have just been updated!",
                    0,"system-company/office-time",0,officeTime.getPersonId(), item, officeTime.getCreateDate());
            notificationRepository.save(notification);
        }

        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<OfficeTimeResponse, Void>> getOfficeTime() throws Exception{
        Long officeTimeId = officeTimeRepository.getPresentOfficeTimeId();
        Optional<OfficeTime> officeTimeDB = officeTimeRepository.findOfficeTimeByOfficeTimeId(officeTimeId);

        if(!officeTimeDB.isPresent()){
            throw new Exception("OfficeTime not exist");
        }
        OfficeTimeResponse officeTimeResponse = new OfficeTimeResponse(officeTimeDB.get());

        ResponseEntity<BaseResponse<OfficeTimeResponse, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(officeTimeResponse, null);
        return responseEntity;
    }
}
