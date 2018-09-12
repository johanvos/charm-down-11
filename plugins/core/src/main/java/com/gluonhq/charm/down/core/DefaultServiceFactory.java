/*
 * Copyright (c) 2016, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.charm.down.core;

import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DefaultServiceFactory<T> implements ServiceFactory<T> {

    private final Class<T> serviceType;
    private final String serviceName;
    private T instance;
        
    public DefaultServiceFactory(Class<T> serviceType) {
        this.serviceType = serviceType;
        String pkg = serviceType.getPackageName();
        serviceName = pkg.substring(pkg.lastIndexOf(".") + 1);
        System.out.println("SERVICENAME = "+serviceName);
    }

    @Override
    public Class<T> getServiceType() {
        return serviceType;
    }

    @Override
    public Optional<T> getInstance() {
        System.out.println("Need to get instance, instance = "+instance);
        if (instance == null) {
            instance = createInstance(Platform.getCurrent());
        }
        System.out.println("Needed to get instance, instance = "+instance);

        return Optional.ofNullable(instance);
    }
    
    private T createInstance(Platform platform) {
        String fqn = "com.gluonhq.charm.down.plugins."+serviceName+"." + platform.name().toLowerCase(Locale.ROOT) + "." + platform.getName() + serviceType.getSimpleName();
        try {
            System.out.println("FQN = "+fqn);
            Module unm = DefaultServiceFactory.class.getClassLoader().getUnnamedModule();
            System.out.println("UNNAMED MODULEzzz? "+unm);

            Class<T> clazz = (Class<T>) Class.forName(fqn);
            if (clazz == null) {
                Module m2 = ClassLoader.getSystemClassLoader().getUnnamedModule();
                System.out.println("M2 = "+m2);
                clazz = (Class<T>) Class.forName(fqn);
                System.out.println("c2 = "+clazz);

            }
            System.out.println("Clazz = "+clazz);
            if (clazz != null) {
                return clazz.newInstance();
            }
         //   clazz = (Class<T>)Class.forName(unm, fqn);
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DefaultServiceFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
