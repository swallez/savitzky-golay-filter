/*
 * Copyright [2009] [Marcin Rzeźnicki]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package mr.go.sgfilter.vis;

import java.util.EventListener;

public interface PlotChangeListener extends EventListener {

    void colorChanged(PlotChangedEvent ev);

    void dataChanged(PlotChangedEvent ev);

    void dataChanged(PlotChangedEvent ev, int index);

    void isLineChanged(PlotChangedEvent ev);

    void maximaChanged();

    void plotAdded(PlotChangedEvent ev);

    void plotRemoved(PlotChangedEvent ev);

    void plotsRemoved();

    void visibilityChanged(PlotChangedEvent ev);

}
