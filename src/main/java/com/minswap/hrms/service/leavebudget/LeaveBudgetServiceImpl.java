package com.minswap.hrms.service.leavebudget;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.DecimalFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LeaveBudgetServiceImpl implements LeaveBudgetService {
    @Autowired
    LeaveBudgetRepository leaveBudgetRepository;

    @Autowired
    PersonRepository personRepository;

    @Override
    public void createLeaveBudget() {
        List<Person> personList = personRepository.findByRankIdIsNot(CommonConstant.RANK_ID_OF_INTERN);
        List<LeaveBudget> leaveBudgetList = new ArrayList<>();
        if(!personList.isEmpty()) {
            for (Person person : personList) {
                leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 0, 0, 0, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[0]));
                leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 180, 0, 0, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[1]));
                leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 20, 0, 0, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[2]));
                leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 70, 0, 0, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[3]));
                leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 3, 0, 0, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[4]));
            }
            leaveBudgetRepository.saveAll(leaveBudgetList);
        }
    }

    @Override
    public void updateLeaveBudgetEachMonth() {
        DecimalFormat df = new DecimalFormat("#.##");
        List<Person> personList = personRepository.findByRankIdIsNot(CommonConstant.RANK_ID_OF_INTERN);
        if(!personList.isEmpty()) {
            for (Person person : personList) {
                Double increaseLeaveBudget = Double.valueOf(df.format(person.getAnnualLeaveBudget() / 12));
                LeaveBudget preLeaveBudget = leaveBudgetRepository.findByPersonIdAndYearAndRequestTypeId(person.getPersonId(), Year.now(), CommonConstant.REQUEST_TYPE_ID_OF_ANNUAL_LEAVE);
                if(preLeaveBudget != null) {
                    preLeaveBudget.setLeaveBudget(preLeaveBudget.getLeaveBudget() + increaseLeaveBudget);
                    leaveBudgetRepository.save(preLeaveBudget);
                }
            }
        }
    }

}
