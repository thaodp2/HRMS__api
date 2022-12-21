package com.minswap.hrms.service.payroll;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.NotificationRepository;
import com.minswap.hrms.repsotories.PayrollRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.PayrollResponse;
import com.minswap.hrms.response.dto.PayrollDisplayDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.email.EmailSenderService;
import com.minswap.hrms.service.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Year;
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

    @Override
    public ResponseEntity<BaseResponse<PayrollResponse, Void>> getDetailPayroll(int month, int year, Long personId, String currentCode) {

        Optional<Person> person = personRepository.findById(personId);
        if(!person.isPresent()){
            throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
        }
        String pinCode = person.get().getPinCode() == null ? "" : person.get().getPinCode();
        if (!pinCode.equalsIgnoreCase(currentCode)) {
            throw new BaseException(ErrorCode.HAVE_NO_PERMISSION);
        }
        Optional<Salary> salaryFromDB = payrollRepository.findByPersonIdAndMonthAndYear(personId, month, Year.of(year));
        if(!salaryFromDB.isPresent()){
            throw new BaseException(ErrorCode.PAYSLIP_NOT_EXIST);
        }
        PayrollDisplayDto payrollDisplayDto = new PayrollDisplayDto();
        Salary salary = salaryFromDB.get();
        payrollDisplayDto.setPersonId(personId);
        payrollDisplayDto.setPersonName(person.get().getFullName());
        payrollDisplayDto.setRollNumber(person.get().getRollNumber());
        payrollDisplayDto.setActualWork(salary.getActualWork());
        payrollDisplayDto.setTotalWork(salary.getTotalWork());
        payrollDisplayDto.setBasicSalary(String.format("%,.0f",Double.valueOf(salary.getBasicSalary())));
        payrollDisplayDto.setOtSalary(String.format("%,.0f",Double.valueOf(salary.getOtSalary())));
        payrollDisplayDto.setFineAmount(String.format("%,.0f",Double.valueOf(salary.getFineAmount())));
        payrollDisplayDto.setSalaryBonus(String.format("%,.0f",Double.valueOf(salary.getBonus())));
        payrollDisplayDto.setTax(String.format("%,.0f",Double.valueOf(salary.getTax())));
        payrollDisplayDto.setSocialInsurance(String.format("%,.0f",Double.valueOf(salary.getSocialInsurance())));
        payrollDisplayDto.setActuallyReceived(String.format("%,.0f",Double.valueOf(salary.getActuallyReceived())));

        PayrollResponse payrollResponse = new PayrollResponse(payrollDisplayDto);
        ResponseEntity<BaseResponse<PayrollResponse, Void>> responseEntity = BaseResponse.ofSucceeded(payrollResponse);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> sendPayrollToEmail(UserPrincipal userPrincipal, int month, int year, String currentCode) {
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity = null;
        try {
            Long personId = personService.getPersonInforByEmail(userPrincipal.getEmail()).getPersonId();
            Optional<Person> person = personRepository.findById(personId);
            if(!person.isPresent()){
                throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
            }
            String pinCode = person.get().getPinCode() == null ? "" : person.get().getPinCode();
            if (!pinCode.equalsIgnoreCase(currentCode)) {
                throw new BaseException(ErrorCode.HAVE_NO_PERMISSION);
            }
            Optional<Salary> salaryFromDB = payrollRepository.findByPersonIdAndMonthAndYear(personId, month, Year.of(year));
            if(!salaryFromDB.isPresent()){
                throw new BaseException(ErrorCode.PAYSLIP_NOT_EXIST);
            }

            PayrollDisplayDto payrollDisplayDto = new PayrollDisplayDto();
            Salary salary = salaryFromDB.get();
            payrollDisplayDto.setPersonId(personId);
            payrollDisplayDto.setPersonName(person.get().getFullName());
            payrollDisplayDto.setRollNumber(person.get().getRollNumber());
            payrollDisplayDto.setActualWork(salary.getActualWork());
            payrollDisplayDto.setTotalWork(salary.getTotalWork());
            payrollDisplayDto.setBasicSalary(String.format("%,.0f",Double.valueOf(salary.getBasicSalary())));
            payrollDisplayDto.setOtSalary(String.format("%,.0f",Double.valueOf(salary.getOtSalary())));
            payrollDisplayDto.setFineAmount(String.format("%,.0f",Double.valueOf(salary.getFineAmount())));
            payrollDisplayDto.setSalaryBonus(String.format("%,.0f",Double.valueOf(salary.getBonus())));
            payrollDisplayDto.setTax(String.format("%,.0f",Double.valueOf(salary.getTax())));
            payrollDisplayDto.setSocialInsurance(String.format("%,.0f",Double.valueOf(salary.getSocialInsurance())));
            payrollDisplayDto.setActuallyReceived(String.format("%,.0f",Double.valueOf(salary.getActuallyReceived())));

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
