package org.sunbird.portal.department.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sunbird.portal.department.dto.DepartmentType;
import org.sunbird.portal.department.repo.DepartmentTypeRepository;

import org.sunbird.common.util.DataValidator;

@Service
public class DepartmentTypeServiceImpl implements DepartmentTypeService {

	@Autowired
	private DepartmentTypeRepository deptTypeRepo;

	@Override
	public Map<String, List<DepartmentType>> getAllDepartmentTypes() {

		Iterable<DepartmentType> deptTypes = deptTypeRepo.findAll();
		if (!DataValidator.isCollectionEmpty(deptTypes)) {
			Map<String, List<DepartmentType>> retValue = new HashMap<String, List<DepartmentType>>();
			Iterator<DepartmentType> it = deptTypes.iterator();
			while (it.hasNext()) {
				DepartmentType dType = it.next();
				if (retValue.containsKey(dType.getDeptType())) {
					retValue.get(dType.getDeptType()).add(dType);
				} else {
					List<DepartmentType> deptTypeList = new ArrayList<DepartmentType>();
					deptTypeList.add(dType);
					retValue.put(dType.getDeptType(), deptTypeList);
				}
			}

			return retValue;
		}

		return null;
	}

	@Override
	public List<DepartmentType> getDepartmentByType(String deptType) {
		return deptTypeRepo.findByDeptTypeIgnoreCase(deptType);
	}

	@Override
	public DepartmentType getDepartmentTypeById(Integer id) {
		return deptTypeRepo.findById(id).get();
	}

	@Override
	public Map<String, List<String>> getDepartmentTypeNames() {
		Iterable<DepartmentType> deptTypes = deptTypeRepo.findAll();
		if (!DataValidator.isCollectionEmpty(deptTypes)) {
			Map<String, List<String>> retValue = new HashMap<String, List<String>>();
//			for (Object o : deptTypes) {
//				Object[] dTypes = (Object[]) o;
//				String deptType = (String) dTypes[0];
//				String deptSubType = (String) dTypes[1];
//				if (retValue.containsKey(deptType)) {
//					retValue.get(deptType).add(deptSubType);
//				} else {
//					List<String> deptSubTypes = new ArrayList<String>();
//					deptSubTypes.add(deptSubType);
//					retValue.put(deptType, deptSubTypes);
//				}
//			}
			for(DepartmentType dType : deptTypes) {
				if (retValue.containsKey(dType.getDeptType())) {
					retValue.get(dType.getDeptType()).add(dType.getDeptSubType());
				} else {
					List<String> deptTypeList = new ArrayList<String>();
					deptTypeList.add(dType.getDeptSubType());
					retValue.put(dType.getDeptType(), deptTypeList);
				}
			}
			return retValue;
		}
		return null;
	}
}
