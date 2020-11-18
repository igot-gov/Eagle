/*
 *                "Copyright 2020 Infosys Ltd.
 *                Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
 *                This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3"
 *
 */

package com.infosys.hubservices.profile.handler;

import java.util.Map;

public interface IProfileRequestHandler {

    public RegistryRequest createRequest(String uuid, Map<String,Object> request);
    public RegistryRequest updateRequest(String uuid, Map<String,Object> request);

    public RegistryRequest searchRequest(String uuid);
    public RegistryRequest readRequest(String id);


}
