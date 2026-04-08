package src.repository.user.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import src.controller.user.customer.request.CreateCustomerRequest;
import src.controller.user.customer.request.UpdateCustomerRequest;
import src.core.exception.DataNotFoundException;
import src.repository.license.DrivingLicenseTypeEntityService;
import src.service.image.user.UserImageService;
import src.service.user.model.DefaultUserStatus;

import java.util.List;

import static src.core.exception.type.NotFoundExceptionType.CUSTOMER_DATA_NOT_FOUND;
import static src.service.user.model.DefaultUserStatus.PENDING_VERIFYING;

@RequiredArgsConstructor
@Service
public class CustomerEntityServiceImpl implements CustomerEntityService {

    private final CustomerRepository repository;
    private final DrivingLicenseTypeEntityService drivingLicenseTypeEntityService;
    private final UserImageService userImageService;

    @Override
    public CustomerEntity create(CreateCustomerRequest createCustomerRequest) {
        CustomerEntity customerEntity = CustomerEntity.customerBuilder()
                .name(createCustomerRequest.getName())
                .surname(createCustomerRequest.getSurname())
                .emailAddress(createCustomerRequest.getEmailAddress())
                .password(createCustomerRequest.getPassword())
                .phoneNumber(createCustomerRequest.getPhoneNumber())
                .drivingLicenseNumber(createCustomerRequest.getDrivingLicenseNumber())
                .drivingLicenseTypeEntity(drivingLicenseTypeEntityService.getById(
                        createCustomerRequest.getDrivingLicenseTypeEntityId()))
                .userImageEntity(userImageService.getById(createCustomerRequest.getUserImageEntityId()))
                .status(PENDING_VERIFYING)
                .build();
        return repository.save(customerEntity);
    }

    @Override
    public CustomerEntity update(UpdateCustomerRequest updateCustomerRequest) {
        CustomerEntity existing = repository.findById(updateCustomerRequest.getId())
                .orElseThrow(() -> new DataNotFoundException(CUSTOMER_DATA_NOT_FOUND));

        if (updateCustomerRequest.getName() != null) existing.setName(updateCustomerRequest.getName());
        if (updateCustomerRequest.getSurname() != null) existing.setSurname(updateCustomerRequest.getSurname());
        if (updateCustomerRequest.getEmailAddress() != null) existing.setEmailAddress(updateCustomerRequest.getEmailAddress());
        if (updateCustomerRequest.getPassword() != null) existing.setPassword(updateCustomerRequest.getPassword());
        if (updateCustomerRequest.getPhoneNumber() != null) existing.setPhoneNumber(updateCustomerRequest.getPhoneNumber());
        if (updateCustomerRequest.getDrivingLicenseNumber() != null) existing.setDrivingLicenseNumber(updateCustomerRequest.getDrivingLicenseNumber());
        if (updateCustomerRequest.getDrivingLicenseTypeEntityId() > 0) {
            existing.setDrivingLicenseTypeEntity(drivingLicenseTypeEntityService.getById(updateCustomerRequest.getDrivingLicenseTypeEntityId()));
        }
        if (updateCustomerRequest.getUserImageEntityId() > 0) {
            existing.setUserImageEntity(userImageService.getById(updateCustomerRequest.getUserImageEntityId()));
        }
        if (updateCustomerRequest.getStatus() != null) existing.setStatus(updateCustomerRequest.getStatus());

        return repository.save(existing);
    }

    @Override
    public CustomerEntity update(CustomerEntity customerEntity) {
        return repository.save(customerEntity);
    }

    @Override
    public CustomerEntity getById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(CUSTOMER_DATA_NOT_FOUND));
    }

    @Override
    public CustomerEntity getByEmailAddress(String emailAddress) {
        return repository.findByEmailAddress(emailAddress).orElseThrow(() -> new DataNotFoundException(
                CUSTOMER_DATA_NOT_FOUND));
    }

    @Override
    public List<CustomerEntity> getAllByDeletedState(boolean isDeleted) {
        return repository.findAllByIsDeleted(isDeleted);
    }

    @Override
    public int getCountByDeletedState(boolean isDeleted) {
        return repository.countByIsDeleted(isDeleted);
    }

    @Override
    public int getCountByStatus(String status) {
        return repository.countByStatus(DefaultUserStatus.valueOf(status.toUpperCase().trim()));
    }

    @Override
    public void delete(CustomerEntity customerEntity) {
        repository.delete(customerEntity);
    }


    @Override
    public List<CustomerEntity> getAll() {
        return repository.findAll();
    }


}
