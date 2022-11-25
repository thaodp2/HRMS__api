package com.minswap.hrms.service.otbudget;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.entities.OTBudget;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OTBudgetServiceImpl implements OTBudgetService {
    @Autowired
    OTBudgetRepository otBudgetRepository;

    @Autowired
    PersonRepository personRepository;

    @Override
    public void createOTBudgetEachMonth() {
        List<Person> personList = personRepository.findAll();
        List<OTBudget> otBudgetList = new ArrayList<>();
        Integer preMonth = null;
        if (java.time.LocalDateTime.now().getMonthValue() != 1) {
            preMonth = java.time.LocalDateTime.now().getMonthValue() - 1;
        }
        if (!personList.isEmpty()) {
            for (Person person : personList) {
                if (java.time.LocalDateTime.now().getMonthValue() != 1) {
                    OTBudget preOTBudget = otBudgetRepository.findByPersonIdAndMonthAndYear(person.getPersonId(), preMonth, Year.now());
                    otBudgetList.add(new OTBudget(person.getPersonId(), preOTBudget.getTimeRemainingOfYear() < 40 ? preOTBudget.getTimeRemainingOfYear() : 40, 0, preOTBudget.getTimeRemainingOfYear() < 40 ? preOTBudget.getTimeRemainingOfYear() : 40,preOTBudget.getTimeRemainingOfYear(), java.time.LocalDateTime.now().getMonthValue(), Year.now()));
                }else {
                    otBudgetList.add(new OTBudget(person.getPersonId(), 40,0,40,200, java.time.LocalDateTime.now().getMonthValue(), Year.now()));
                }
            }
            otBudgetRepository.saveAll(otBudgetList);
        }
    }
}
