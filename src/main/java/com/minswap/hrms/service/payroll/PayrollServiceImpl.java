package com.minswap.hrms.service.payroll;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Notification;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.PayrollRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.response.dto.PayrollDisplayDto;
import com.minswap.hrms.response.dto.PayrollDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.email.EmailSenderService;
import com.minswap.hrms.service.person.PersonService;
import com.minswap.hrms.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PayrollServiceImpl implements PayrollService{
    @Autowired
    PayrollRepository  payrollRepository;

    HttpStatus httpStatus;
    @Autowired
    PersonRepository personRepository;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    PersonService personService;

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
            if (payrollDto.getBasicSalary() <= 5000000D) {
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
                String additionalWork = payrollRepository.getAdditionalWorkInRequest(date, personId);
                if (additionalWork != null){
                    additionalWork.replaceAll("-","");
                    Double hour = (Double.valueOf(additionalWork.substring(0,2)));
                    if(hour > 8L){
                        hour = hour - 8L;
                    }
                    Double minute = Double.valueOf(additionalWork.substring(3,5));
                    workTime = (workTime + (hour + (minute/60)))/8;
                    actualWork = actualWork + workTime;
                }
            }
            startDate.add(Calendar.DATE, 1);
        }
        return actualWork;
    }

    @Override
    public ResponseEntity<BaseResponse<PayrollResponse, Void>> getDetailPayroll(int month, int year, Long personId) {

        Optional<Salary> salaryFromDB = payrollRepository.findByPersonIdAndMonthAndYear(personId, month, Year.of(year));
        if(!salaryFromDB.isPresent()){
            throw new BaseException(ErrorCode.newErrorCode(404,
                                                        "Salary not found!",
                                                                httpStatus.NOT_FOUND));
        }
        Optional<Person> person = personRepository.findById(personId);
        if(!person.isPresent()){
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "Person not found!",
                    httpStatus.NOT_FOUND));
        }
        PayrollDisplayDto payrollDisplayDto = new PayrollDisplayDto();
        Salary salary = salaryFromDB.get();
        payrollDisplayDto.setPersonId(personId);
        payrollDisplayDto.setPersonName(person.get().getFullName());
        payrollDisplayDto.setRollNumber(person.get().getRollNumber());
        payrollDisplayDto.setActualWork(salary.getActualWork());
        payrollDisplayDto.setTotalWork(salary.getTotalWork());
        payrollDisplayDto.setBasicSalary(String.format("%.0f",Double.valueOf(salary.getBasicSalary())));
        payrollDisplayDto.setOtSalary(String.format("%.0f",Double.valueOf(salary.getOtSalary())));
        payrollDisplayDto.setFineAmount(String.format("%.0f",Double.valueOf(salary.getFineAmount())));
        payrollDisplayDto.setSalaryBonus(String.format("%.0f",Double.valueOf(salary.getBonus())));
        payrollDisplayDto.setTax(String.format("%.0f",Double.valueOf(salary.getTax())));
        payrollDisplayDto.setSocialInsurance(String.format("%.0f",Double.valueOf(salary.getSocialInsurance())));
        payrollDisplayDto.setActuallyReceived(String.format("%.0f",Double.valueOf(salary.getActuallyReceived())));

        PayrollResponse payrollResponse = new PayrollResponse(payrollDisplayDto);
        ResponseEntity<BaseResponse<PayrollResponse, Void>> responseEntity = BaseResponse.ofSucceeded(payrollResponse);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(UserPrincipal userPrincipal, int month, int year) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        try {
            Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
            Optional<Salary> salaryFromDB = payrollRepository.findByPersonIdAndMonthAndYear(personId, month, Year.of(year));
            if(!salaryFromDB.isPresent()){
                throw new BaseException(ErrorCode.newErrorCode(404,
                        "Salary not found!",
                        httpStatus.NOT_FOUND));
            }
            Optional<Person> person = personRepository.findById(personId);
            if(!person.isPresent()){
                throw new BaseException(ErrorCode.newErrorCode(404,
                        "Person not found!",
                        httpStatus.NOT_FOUND));
            }
            PayrollDisplayDto payrollDisplayDto = new PayrollDisplayDto();
            Salary salary = salaryFromDB.get();
            payrollDisplayDto.setPersonId(personId);
            payrollDisplayDto.setPersonName(person.get().getFullName());
            payrollDisplayDto.setRollNumber(person.get().getRollNumber());
            payrollDisplayDto.setActualWork(salary.getActualWork());
            payrollDisplayDto.setTotalWork(salary.getTotalWork());
            payrollDisplayDto.setBasicSalary(String.format("%.0f",Double.valueOf(salary.getBasicSalary())));
            payrollDisplayDto.setOtSalary(String.format("%.0f",Double.valueOf(salary.getOtSalary())));
            payrollDisplayDto.setFineAmount(String.format("%.0f",Double.valueOf(salary.getFineAmount())));
            payrollDisplayDto.setSalaryBonus(String.format("%.0f",Double.valueOf(salary.getBonus())));
            payrollDisplayDto.setTax(String.format("%.0f",Double.valueOf(salary.getTax())));
            payrollDisplayDto.setSocialInsurance(String.format("%.0f",Double.valueOf(salary.getSocialInsurance())));
            payrollDisplayDto.setActuallyReceived(String.format("%.0f",Double.valueOf(salary.getActuallyReceived())));

            String body = "Total work day: " + payrollDisplayDto.getTotalWork() + "\n";
            body += "Actual work day: " + payrollDisplayDto.getActualWork() + "\n";
            body += "Earnings" + "\n";
            body += "\t"+"Basic salary: " + payrollDisplayDto.getBasicSalary() + "\n";
            body += "\t"+"Bonus salary: " + payrollDisplayDto.getSalaryBonus() + "\n";
            body += "\t"+"OT salary: " + payrollDisplayDto.getOtSalary() + "\n";
            body += "Deductions" + "\n";
            body += "\t"+"Tax: " + payrollDisplayDto.getTax() + "\n";
            body += "\t"+"Social Insurance (10.5%): " + payrollDisplayDto.getSocialInsurance() + "\n";
//            body += "\t"+"Fine Amount: " + payrollDisplayDto.getFineAmount() + "\n\n";
            body += "Net Income: " + payrollDisplayDto.getActuallyReceived() + "\n";

            //send mail
            String toMail = userPrincipal.getEmail();
            emailSenderService.sendEmail(toMail, "Payslip " + month + "/" + year, body);
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        }catch (Exception e){
            responseEntity = BaseResponse.ofSucceededOffset(HttpStatus.EXPECTATION_FAILED, null);
        }
        return responseEntity;
    }
}
