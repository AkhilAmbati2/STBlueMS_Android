/*
 * Copyright (c) 2017  STMicroelectronics – All rights reserved
 * The STMicroelectronics corporate logo is a trademark of STMicroelectronics
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice, this list of conditions
 *   and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice, this list of
 *   conditions and the following disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - Neither the name nor trademarks of STMicroelectronics International N.V. nor any other
 *   STMicroelectronics company nor the names of its contributors may be used to endorse or
 *   promote products derived from this software without specific prior written permission.
 *
 * - All of the icons, pictures, logos and other images that are provided with the source code
 *   in a directory whose title begins with st_images may only be used for internal purposes and
 *   shall not be redistributed to any third party or modified in any way.
 *
 * - Any redistributions in binary form shall not include the capability to display any of the
 *   icons, pictures, logos and other images that are provided with the source code in a directory
 *   whose title begins with st_images.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */

/*package com.st.blesensor.cloud.GenericMqtt;*/
package com.st.blesensor.cloud.TagoIO;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.st.BlueSTSDK.gui.util.InputChecker.CheckNotEmpty;
import com.st.blesensor.cloud.CloudIotClientConfigurationFactory;
import com.st.blesensor.cloud.CloudIotClientConnectionFactory;
import com.st.blesensor.cloud.R;
import com.st.blesensor.cloud.util.MqttClientUtil;
import com.st.BlueSTSDK.Node;

/**
 * Ask the parameters for a generic mqtt broker
 */
public class TagoIOConfigurationFactory implements CloudIotClientConfigurationFactory {
    private static final String FACTORY_NAME = "TagoIO";


    private static final String CONF_PREFERENCE = TagoIOConfigurationFactory.class.getCanonicalName();
    private static final String TOKEN_KEY = CONF_PREFERENCE+".TOKEN_KEY";
    private static final String CLIENT_ID_KEY = CONF_PREFERENCE+".CLIENT_ID_KEY";;

    private EditText mTokenText;
    private EditText mClientIdText;

    /**
     * if present load the previous connection data from the app preferences
     * @param pref object where read the preference
     */
    private void loadFromPreferences(SharedPreferences pref){
        mTokenText.setText(pref.getString(TOKEN_KEY,""));
        mClientIdText.setText(pref.getString(CLIENT_ID_KEY,""));
    }

    /**
     * store the connection parameter into a preference object
     * @param pref object where store the preference
     */
    private void storeToPreference(SharedPreferences pref){
        pref.edit()
                .putString(TOKEN_KEY,mTokenText.getText().toString())
                .putString(CLIENT_ID_KEY,mClientIdText.getText().toString())
                .apply();
    }

    @Override
    public void attachParameterConfiguration(Context c, ViewGroup root) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.cloud_config_tagoio,root);

        TextInputLayout passwordLayout = v.findViewById(R.id.tagoio_tokenLayout);
        mTokenText = passwordLayout.getEditText();
        mTokenText.addTextChangedListener(new CheckNotEmpty(passwordLayout,R.string.tagoio_tokenEmpty));
        TextInputLayout clientIdLayout = v.findViewById(R.id.tagoio_deviceIdLayout);
        mClientIdText = clientIdLayout.getEditText();
        mClientIdText.addTextChangedListener(new CheckNotEmpty(clientIdLayout,R.string.tagoio_deviceIdEmpty));

        loadFromPreferences(c.getSharedPreferences(CONF_PREFERENCE,Context.MODE_PRIVATE));

    }

    @Override
    public void loadDefaultParameters(@Nullable Node n) {
        if(n==null)
            return;
        if(mClientIdText.getText().length()==0)
            mClientIdText.setText(MqttClientUtil.getDefaultCloudDeviceName(n));
    }

    @Override
    public String getName() {
        return FACTORY_NAME;
    }

    @Override
    public CloudIotClientConnectionFactory getConnectionFactory() throws IllegalArgumentException {

        String password = mTokenText.getText().toString();
        if(password.isEmpty()){
            throw  new IllegalArgumentException("Invalid empty passowrd");
        }

        String clientId = mClientIdText.getText().toString();
        if(clientId.isEmpty()){
            throw  new IllegalArgumentException("Invalid empty clientID");
        }

        storeToPreference(mTokenText.getContext().getSharedPreferences(CONF_PREFERENCE,Context.MODE_PRIVATE));
        return new TagoIOFactory(
                mClientIdText.getText().toString(),
                mTokenText.getText().toString());
    }
}