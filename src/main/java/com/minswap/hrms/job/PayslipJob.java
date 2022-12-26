package com.minswap.hrms.job;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.controller.NotificationController;
import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.PayrollRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.dto.PayrollDto;
import com.minswap.hrms.service.notification.NotificationService;
import com.minswap.hrms.util.DateTimeUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class PayslipJob {

    @Autowired
    PayrollRepository payrollRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Scheduled(cron = "* 0 1 1 * *")
    public void  cronjobUpdateSalary() throws ParseException {

        List<Long> allPersonId = personRepository.getAllPersonId();
        for (Long personId : allPersonId) {
            int month = Calendar.getInstance().get(Calendar.MONTH);
            int year = Calendar.getInstance().get(Calendar.YEAR);
            if(month == 0){
                month = 12;
                year--;
            }
            Double totalWorks = this.getTotalWorkOfMonth(month, year);
            Double actualWorks = this.getActualWorkOfMonth(month, year, personId);
            PayrollDto payrollDto = payrollRepository.getDataSalary(month, Year.of(year), personId);
            //Check null and calculate salary
            Double otSalary = payrollDto.getOtSalary() == null ? 0 : payrollDto.getOtSalary();
            otSalary = otSalary * 2 * ((payrollDto.getSalaryBonus() / totalWorks) / 8);

            Double bonusSalary = payrollDto.getSalaryBonus() == null ? 0 : payrollDto.getSalaryBonus();
            bonusSalary = (bonusSalary/totalWorks) * actualWorks;

            Double tax = 0D;
            Double basicSalary = payrollDto.getBasicSalary() == null ? 0 : payrollDto.getBasicSalary();
            basicSalary = (basicSalary/totalWorks) * actualWorks;
            if (basicSalary <= 5000000D) {
                tax = (basicSalary * 5) / 100;
            } else if (basicSalary <= 10000000D) {
                tax = (basicSalary * 10) / 100 - 250000D;
            } else if (basicSalary <= 18000000D) {
                tax = (basicSalary * 15) / 100 - 750000D;
            }else if (basicSalary <= 32000000D) {
                tax = (basicSalary * 20) / 100 - 1650000D;
            }else if (basicSalary<= 52000000D) {
                tax = (basicSalary * 25) / 100 - 3250000D;
            }else if (basicSalary<= 80000000D) {
                tax = (basicSalary * 30) / 100 - 5850000D;
            }else {
                tax = (basicSalary * 35) / 100 - 9850000D;
            }

            Double fineAmount = payrollDto.getFineAmount() == null ? 0 :  payrollDto.getFineAmount();
            Double socialInsurance = (basicSalary * 10.5)/100;
            Double actuallyReceived = basicSalary + otSalary + bonusSalary - fineAmount - tax - socialInsurance ;

            Salary salary = new Salary();

            salary.setPersonId(personId);
            salary.setTotalWork(totalWorks);
            salary.setActualWork(actualWorks);
            salary.setBasicSalary(String.format("%.0f",basicSalary));
            salary.setOtSalary(String.format("%.0f",otSalary));
            salary.setFineAmount(String.format("%.0f",fineAmount));
            salary.setBonus(String.format("%.0f",bonusSalary));
            salary.setTax(String.format("%.0f",tax));
            salary.setSocialInsurance(String.format("%.0f",socialInsurance));
            salary.setActuallyReceived(String.format("%.0f",actuallyReceived));
            salary.setMonth(month);
            salary.setYear(Year.of(year));
            salary.setSalaryId(0L);
            Optional<Salary> salaryOptional = payrollRepository.getFirstByMonthAndYearAndPersonId(month, Year.of(year), personId);
            if (salaryOptional.isPresent()){
                salary.setSalaryId(salaryOptional.get().getSalaryId());
            }
            payrollRepository.save(salary);
            Date currentDate = DateTimeUtil.getCurrentTime();
            currentDate.setTime(currentDate.getTime() + CommonConstant.MILLISECOND_7_HOURS);
            Notification notification = new Notification("Salary slip of month " + month + "/" + year + " already available! " ,
                    0, "emp-self-service/payslip" , 0, null, personId, currentDate);
            notificationRepository.save(notification);
            notificationService.send(notification);
        }
    }

    public Double getTotalWorkOfMonth(int month, int year){

        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.MONTH, month - 1);
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.DATE, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.MONTH, month - 1);
        endDate.set(Calendar.YEAR, year);
        int lastDate = endDate.getActualMaximum(Calendar.DATE);
        endDate.set(Calendar.DATE, lastDate);

        Double workingDays = 0D;
        while(!startDate.after(endDate))
        {
            int day = startDate.get(Calendar.DAY_OF_WEEK);
            if ((day != Calendar.SATURDAY) && (day != Calendar.SUNDAY))
                workingDays++;
            startDate.add(Calendar.DATE, 1);
        }
        return workingDays;
    }

    public Double getActualWorkOfMonth(int month, int year, Long personId){

        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.MONTH, month - 1);
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.DATE, 1);

        Calendar endDate = Calendar.getInstance();
        endDate.set(Calendar.MONTH, month - 1);
        endDate.set(Calendar.YEAR, year);
        int lastDate = endDate.getActualMaximum(Calendar.DATE);
        endDate.set(Calendar.DATE, lastDate);

        Double actualWork = 0D;
        while(!startDate.after(endDate))
        {
            Date date = startDate.getTime();
            Double workTime = payrollRepository.getWorkingTime(date, personId);
            if (workTime == null ){
                workTime = 0D;
            }
            if(workTime >= 8D){
                actualWork++;
            }else {
                actualWork += workTime/8;
                Double additionalWork = payrollRepository.getAdditionalWorkInRequest(date, personId);
                if (additionalWork != null){
                    workTime = additionalWork/8;
                    actualWork = actualWork + workTime;
                }
            }
            startDate.add(Calendar.DATE, 1);
        }
        return actualWork;
    }
}
