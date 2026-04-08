package src.service.vehicle.car;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import src.controller.vehicle.car.request.CreateCarRequest;
import src.controller.vehicle.car.request.UpdateCarRequest;
import src.controller.vehicle.car.response.CarResponse;
import src.core.exception.DataNotFoundException;
import src.repository.image.CarImageEntity;
import src.repository.rental.RentalEntity;
import src.repository.vehicle.car.CarEntity;
import src.repository.vehicle.car.CarEntityService;
import src.repository.vehicle.features.common.status.VehicleStatusEntityServiceImpl;
import src.service.image.car.CarImageService;
import src.service.vehicle.features.common.status.model.DefaultVehicleStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static src.controller.AnsiColorConstant.ANSI_BOLD;
import static src.controller.AnsiColorConstant.ANSI_RESET;
import static src.core.exception.type.NotFoundExceptionType.VEHICLE_STATUS_NOT_FOUND;
import static src.service.vehicle.features.common.status.model.DefaultVehicleStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarServiceImpl implements CarService {

    private final CarEntityService entityService;
    private final CarRules rules;
    private final VehicleStatusEntityServiceImpl vehicleStatusManager;
    private final CarImageService carImageService;

    @Override
    public void create(CreateCarRequest createCarRequest) throws IOException {
        createCarRequest = rules.fix(createCarRequest);
        rules.check(createCarRequest);
        try {
            entityService.create(createCarRequest);
        } catch (Exception e) {
            carImageService.delete(createCarRequest.getCarImageEntityId());
            throw e;
        }
    }

    @Override
    public CarResponse getById(int id) {
        return entityService.getById(id).toModel();
    }

    @Override
    public CarResponse update(UpdateCarRequest updateCarRequest) throws IOException {
        CarEntity existing = entityService.getById(updateCarRequest.getId());

        boolean isFullUpdate = updateCarRequest.getBrandEntityId() > 0
                && updateCarRequest.getCarModelEntityId() > 0
                && updateCarRequest.getLicensePlate() != null
                && !updateCarRequest.getLicensePlate().isBlank();

        if (isFullUpdate) {
            updateCarRequest = rules.fix(updateCarRequest);
            rules.check(updateCarRequest);
            CarImageEntity carImage = carImageService.getById(updateCarRequest.getCarImageEntityId());
            if (carImage.getId() != updateCarRequest.getCarImageEntityId()) {
                carImageService.delete(carImage.getId());
            }
            return entityService.update(updateCarRequest).toModel();
        }

        if (updateCarRequest.getRentalPrice() > 0) {
            existing.setRentalPrice(updateCarRequest.getRentalPrice());
        }
        if (updateCarRequest.getDetails() != null) {
            existing.setDetails(updateCarRequest.getDetails());
        }
        existing.setAvailable(updateCarRequest.isAvailable());

        return entityService.update(existing).toModel();
    }

    @Transactional
    @Override
    public List<CarResponse> getAll() {
        return markAllForDrivingLicenseSuitable(entityService.getAll(), null);
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByDeletedState(boolean isDeleted) {
        return mapToDTOList(entityService.getAllByDeletedState(isDeleted));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByStatus(Integer statusId) {
        return mapToDTOList(entityService.getAllByStatus(statusId));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByColorId(int id) {
        return mapToDTOList(entityService.getAllByColorId(id));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByModelId(int id) {
        return mapToDTOList(entityService.getAllByModelId(id));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByBrandId(int brandId) {
        return mapToDTOList(entityService.getAllByBrandId(brandId));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByYearBetween(int startYear, int endYear) {
        return mapToDTOList(entityService.getAllByYearBetween(startYear, endYear));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByRentalPriceBetween(double startPrice, double endPrice) {
        return mapToDTOList(entityService.getAllByRentalPriceBetween(startPrice, endPrice));
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByAvailabilityBetween(LocalDate startDate, LocalDate endDate) {
        List<CarEntity> allCars = entityService.getAll();
        List<CarEntity> availableCars = filterAvailableCars(allCars, startDate, endDate);
        rules.checkDataList(availableCars);
        return mapToDTOList(availableCars);
    }

    @Transactional
    @Override
    public List<CarResponse> getAllByIsDrivingLicenseSuitable(Integer customerId) {
        List<CarEntity> allCars = entityService.getAll();
        List<CarEntity> carsByDrivingLicenseSuitable = getCarEntityListByDrivingLicenseSuitable(allCars, customerId);
        return mapToDTOList(carsByDrivingLicenseSuitable);
    }

    @Transactional
    @Override
    public List<CarResponse> getAllFiltered(Integer customerId, Boolean licenseSuitable,
                                            LocalDate startDate, LocalDate endDate,
                                            Integer startPrice, Integer endPrice,
                                            Boolean isDeleted, Integer statusId,
                                            Integer colorId,
                                            Integer seat, Integer luggage, Integer modelId,
                                            Integer startYear, Integer endYear, Integer brandId,
                                            Integer fuelTypeId, Integer shiftTypeId, Integer segmentId) {
        log.info("filtering cars...");
        List<CarEntity> filteredCars = entityService.getAllFiltered(
                startPrice, endPrice,
                statusId,
                colorId, seat, luggage,
                modelId, startYear,
                endYear, brandId,
                fuelTypeId, shiftTypeId, segmentId);

        filteredCars = filterAvailableCars(filteredCars, startDate, endDate);
        filteredCars = filterDeletedCars(filteredCars, isDeleted);

        if (licenseSuitable != null && licenseSuitable) {
            filteredCars = getCarEntityListByDrivingLicenseSuitable(filteredCars, customerId);
        }
        return markAllForDrivingLicenseSuitable(filteredCars, customerId);
    }

    @Override
    public void delete(int id, boolean hardDelete) throws IOException {

        if (hardDelete) {
            CarEntity carEntity = entityService.getById(id);
            entityService.delete(carEntity);
            carImageService.delete(carEntity.getCarImageEntity().getId());
        } else {
            softDelete(id);
        }
    }

    @Override
    public void softDelete(int id) {
        changeStatus(entityService.getById(id), DELETED);
    }

    @Override
    public void addRental(int carId, RentalEntity rentalEntity) {
        CarEntity carEntity = entityService.getById(carId);
        carEntity.getRentalList().add(rentalEntity);
        entityService.update(carEntity);
        changeStatus(carEntity, BOOKED);
    }

    @Override
    public void removeRental(int carId, RentalEntity rentalEntity) {
        CarEntity carEntity = entityService.getById(carId);
        carEntity.getRentalList().remove(rentalEntity);
        if (carEntity.getRentalList().isEmpty()) {
            changeStatus(carEntity, AVAILABLE);
        }
        entityService.update(carEntity);
    }


    //---------------------------------Local Methods------------------------------------------------------

    public List<CarResponse> markAllForDrivingLicenseSuitable(List<CarEntity> cars, Integer customerId) {
        List<CarResponse> carResponseList = mapToDTOList(cars);
        carResponseList.forEach(carDTO -> {
            boolean isMatched = rules.isDrivingLicenseTypeSuitable(carDTO.getId(), customerId);
            // If the vehicle is not suitable for the user's license type
            // set isLicenseTypeSuitable to false
            // The goal here is to return all vehicles but mark those with incompatible license types.
            carDTO.setIsLicenseTypeSuitable(isMatched);
        });
        return carResponseList;
    }

    private List<CarEntity> filterDeletedCars(List<CarEntity> filteredCars, Boolean isDeleted) {
        if (isDeleted == null) {
            return filteredCars;
        }
        return filteredCars.stream().filter(car -> car.getIsDeleted() == isDeleted).toList();
    }

    public List<CarEntity> getCarEntityListByDrivingLicenseSuitable(List<CarEntity> carEntityList, Integer customerId) {
        //If the customer is logged in and requests only vehicles matching their license, the cars are compared and filtered by the customer's license type.
        return carEntityList.stream().filter(car -> rules.isDrivingLicenseTypeSuitable(car.getId(), customerId)).toList();
    }

    private List<CarEntity> filterAvailableCars(List<CarEntity> cars, LocalDate startDate, LocalDate endDate) {
        return cars.stream()
                .filter(car -> isCarAvailableBetween(car.getId(), startDate, endDate))
                .collect(Collectors.toList());
    }

    public boolean isCarAvailableBetween(int carId, LocalDate startDate, LocalDate endDate) {
        startDate = rules.fixStartDate(startDate);
        endDate = rules.fixEndDate(startDate, endDate);
        rules.checkDates(startDate, endDate);

        CarEntity car = entityService.getById(carId);
        List<RentalEntity> rentalList = car.getRentalList();
        if (car.isAvailable()) {
            for (RentalEntity rental : rentalList) {
                LocalDate rentalStartDate = rental.getStartDate();
                LocalDate rentalEndDate = rental.getEndDate();
                if (!(startDate.isAfter(rentalEndDate)
                        || endDate.isBefore(rentalStartDate))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void changeStatus(CarEntity carEntity, DefaultVehicleStatus status) {
        log.info("changing car status to {} by id: {}", ANSI_BOLD + status.getLabel(), carEntity.getId() + ANSI_RESET);
        boolean available;
        switch (status) {
            case AVAILABLE, BOOKED -> {
                available = true;
            }
            case DELETED -> {
                available = false;
                carEntity.setIsDeleted(true);
                carEntity.setDeletedAt(LocalDateTime.now());
            }
            case IN_USE, MAINTENANCE, UNAVAILABLE -> {
                available = false;
            }
            default -> {
                throw new DataNotFoundException(VEHICLE_STATUS_NOT_FOUND);
            }
        }
        carEntity.setVehicleStatusEntity(vehicleStatusManager.getByStatus(status));
        carEntity.setAvailable(available);
        entityService.update(carEntity);
    }

    @Override
    public int getCountByDeletedState(boolean isDeleted) {
        return entityService.getCountByDeletedState(isDeleted);
    }

    @Override
    public int getCountByStatusId(int statusId) {
        return entityService.getCountByStatusId(statusId);
    }

    private List<CarResponse> mapToDTOList(List<CarEntity> cars) {
        List<CarResponse> carResponseList = cars.stream()
                .map(CarEntity::toModel)
                .toList();

        rules.checkDataList(cars);
        return carResponseList;
    }

}
